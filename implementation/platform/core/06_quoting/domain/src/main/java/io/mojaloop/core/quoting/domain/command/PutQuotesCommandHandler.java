package io.mojaloop.core.quoting.domain.command;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.component.jpa.transaction.TransactionContext;
import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.core.quoting.contract.command.PutQuotesCommand;
import io.mojaloop.core.quoting.contract.exception.ExpirationNotInFutureException;
import io.mojaloop.core.quoting.contract.exception.QuoteCurrenciesMismatchException;
import io.mojaloop.core.quoting.contract.exception.ReceivingAmountMismatchException;
import io.mojaloop.core.quoting.contract.exception.TransferAmountMismatchException;
import io.mojaloop.core.quoting.domain.QuotingDomainConfiguration;
import io.mojaloop.core.quoting.domain.repository.QuoteRepository;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.component.handy.FspiopDates;
import io.mojaloop.fspiop.component.handy.FspiopUrls;
import io.mojaloop.fspiop.service.api.forwarder.ForwardRequest;
import io.mojaloop.fspiop.service.api.quotes.RespondQuotes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.Instant;

@Service
public class PutQuotesCommandHandler implements PutQuotesCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutQuotesCommandHandler.class);

    private final ParticipantStore participantStore;

    private final RespondQuotes respondQuotes;

    private final ForwardRequest forwardRequest;

    private final QuoteRepository quoteRepository;

    private final PlatformTransactionManager transactionManager;

    private final QuotingDomainConfiguration.QuoteSettings quoteSettings;

    public PutQuotesCommandHandler(ParticipantStore participantStore,
                                   RespondQuotes respondQuotes,
                                   ForwardRequest forwardRequest,
                                   QuoteRepository quoteRepository,
                                   PlatformTransactionManager transactionManager,
                                   QuotingDomainConfiguration.QuoteSettings quoteSettings) {

        assert participantStore != null;
        assert respondQuotes != null;
        assert forwardRequest != null;
        assert quoteRepository != null;
        assert transactionManager != null;
        assert quoteSettings != null;

        this.participantStore = participantStore;
        this.respondQuotes = respondQuotes;
        this.forwardRequest = forwardRequest;
        this.quoteRepository = quoteRepository;
        this.transactionManager = transactionManager;
        this.quoteSettings = quoteSettings;
    }

    @Write
    @Override
    public Output execute(Input input) {

        var udfQuoteId = input.udfQuoteId();

        LOGGER.info("({}) Executing PutQuotesCommandHandler with input: [{}]", udfQuoteId.getId(), input);

        var payeeFspCode = new FspCode(input.request().payee().fspCode());
        var payeeFsp = this.participantStore.getFspData(payeeFspCode);
        LOGGER.info("({}) Found payee FSP: [{}]", udfQuoteId.getId(), payeeFsp);

        var payerFspCode = new FspCode(input.request().payer().fspCode());
        var payerFsp = this.participantStore.getFspData(payerFspCode);
        LOGGER.info("({}) Found payer FSP: [{}]", udfQuoteId.getId(), payerFsp);

        try {

            var quoteIdPutResponse = input.quotesIDPutResponse();
            LOGGER.info("({}) quotesIDPutResponse: [{}]", udfQuoteId.getId(), quoteIdPutResponse);

            if (this.quoteSettings.stateful()) {

                TransactionContext.startNew(this.transactionManager, udfQuoteId.getId());
                var optQuote = this.quoteRepository.findOne(QuoteRepository.Filters.withUdfQuoteId(udfQuoteId));

                if (optQuote.isEmpty()) {

                    LOGGER.warn("({}) Receiving non-existence Quote. Just ignore it.", udfQuoteId.getId());
                    LOGGER.info("({}) Returning from PutQuotesCommandHandler (non-existence Quote).", udfQuoteId.getId());

                    return new Output();
                }

                var quote = optQuote.get();
                LOGGER.info("({}) Found Quote object with UDF Quote ID: [{}] , quote : {}", udfQuoteId.getId(), udfQuoteId.getId(), quote);

                var expiration = quoteIdPutResponse.getExpiration();
                Instant responseExpiration = null;

                if (expiration != null) {

                    try {

                        responseExpiration = FspiopDates.fromRequestBody(quoteIdPutResponse.getExpiration());

                    } catch (ParseException e) {

                        LOGGER.error("({}) Wrong expiration format.", udfQuoteId.getId());
                        LOGGER.info("({}) Returning from PutQuotesCommandHandler (wrong expiration).", udfQuoteId.getId());

                        quote.error("Wrong expiration format. Response expiration format : " + quoteIdPutResponse.getExpiration());
                        this.quoteRepository.save(quote);
                        TransactionContext.commit();

                        throw e;
                    }
                }

                var quotedCurrency = quote.getCurrency();
                var transferCurrency = quoteIdPutResponse.getTransferAmount().getCurrency();

                if (!(quotedCurrency == transferCurrency && transferCurrency == quoteIdPutResponse.getPayeeFspFee().getCurrency() &&
                          transferCurrency == quoteIdPutResponse.getPayeeFspCommission().getCurrency() &&
                          transferCurrency == quoteIdPutResponse.getPayeeReceiveAmount().getCurrency())) {

                    LOGGER.error("({}) The currency of quote, transferAmount, payeeFspFee, payeeFspCommission and payeeReceiveAmount must be the same.", udfQuoteId.getId());
                    LOGGER.info("({}) Returning from PutQuotesCommandHandler (currency mismatch).", udfQuoteId.getId());

                    quote.error("The currency of quote, transferAmount, payeeFspFee, payeeFspCommission and payeeReceiveAmount must be the same.");
                    this.quoteRepository.save(quote);
                    TransactionContext.commit();

                    throw new QuoteCurrenciesMismatchException();
                }

                var transferAmount = new BigDecimal(quoteIdPutResponse.getTransferAmount().getAmount());
                var payeeFspFee = new BigDecimal(quoteIdPutResponse.getPayeeFspFee().getAmount());
                var payeeFspCommission = new BigDecimal(quoteIdPutResponse.getPayeeFspCommission().getAmount());
                var payeeReceiveAmount = new BigDecimal(quoteIdPutResponse.getPayeeReceiveAmount().getAmount());

                try {

                    quote.responded(responseExpiration, transferAmount, payeeFspFee, payeeFspCommission, payeeReceiveAmount);
                    this.quoteRepository.save(quote);
                    TransactionContext.commit();

                } catch (TransferAmountMismatchException e) {

                    LOGGER.error("({}) Transfer amount mismatch.", udfQuoteId.getId(), e);
                    quote.error(e.getMessage());
                    this.quoteRepository.save(quote);
                    TransactionContext.commit();

                    throw e;
                } catch (ReceivingAmountMismatchException e) {

                    LOGGER.error("({}) Receiving amount mismatch.", udfQuoteId.getId(), e);
                    quote.error(e.getMessage());
                    this.quoteRepository.save(quote);
                    TransactionContext.commit();

                    throw e;
                } catch (ExpirationNotInFutureException e) {

                    LOGGER.error("({}) Quote expiration.", udfQuoteId.getId(), e);
                    quote.error(e.getMessage());
                    this.quoteRepository.save(quote);
                    TransactionContext.commit();

                    throw e;
                }
            }

            var payerBaseUrl = payerFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
            LOGGER.info("({}) Forwarding request to payer FSP (Url): [{}]", udfQuoteId.getId(), payerFsp);

            this.forwardRequest.forward(payerBaseUrl, input.request());
            LOGGER.info("({}) Done forwarding request to payer FSP (Url): [{}]", udfQuoteId.getId(), payerFsp);

        } catch (FspiopException e) {

            LOGGER.error("FspiopException occurred while executing PutQuotesCommandHandler: [{}]", e.getMessage());
            LOGGER.error("Ignore sending error response back to Payee.");

            // For PUT calls, we must not send back an error to the Payee.
            // Here, Payee side responded with PUT, but Hub cannot forward the request to Payer due to some error.
            // But Hub won't respond with an error to the Payee, only to the Payer.

        } catch (ExpirationNotInFutureException | ReceivingAmountMismatchException | TransferAmountMismatchException | QuoteCurrenciesMismatchException | ParseException e) {

            LOGGER.error("(PayeeError) Exception occurred while executing PutQuotesCommandHandler: [{}]", e.getMessage());

            var sendBackTo = new Payer(payerFspCode.value());
            var baseUrl = payerFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
            var url = FspiopUrls.newUrl(baseUrl, input.request().uri() + "/error");

            try {

                var errorInformationObject = FspiopErrors.GENERIC_PAYEE_ERROR.toErrorObject();
                this.respondQuotes.putQuotesError(sendBackTo, url, errorInformationObject);
                LOGGER.info("({}) (PayeeError) Done sending error response to payer FSP.", udfQuoteId.getId());
                LOGGER.info("({}) (PayeeError) Returning from PutQuotesCommandHandler.", udfQuoteId.getId());

            } catch (FspiopException ignored) {
                LOGGER.error("Something went wrong while sending error response to payer FSP: ", e);
            }

        } catch (Exception e) {

            LOGGER.error("Exception occurred while executing PutQuotesCommandHandler: [{}]", e.getMessage());

            var sendBackTo = new Payer(payerFspCode.value());
            var baseUrl = payerFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
            var url = FspiopUrls.newUrl(baseUrl, input.request().uri() + "/error");

            try {

                var errorInformationObject = FspiopErrors.GENERIC_SERVER_ERROR.toErrorObject();
                this.respondQuotes.putQuotesError(sendBackTo, url, errorInformationObject);
                LOGGER.info("({}) (Exception) Done sending error response to payer FSP.", udfQuoteId.getId());
                LOGGER.info("({}) (Exception) Returning from PutQuotesCommandHandler.", udfQuoteId.getId());

            } catch (FspiopException ignored) {
                LOGGER.error("Something went wrong while sending error response to payer FSP: ", e);
            }
        }

        LOGGER.info("Returning from PutQuotesCommandHandler successfully.");
        return new Output();
    }

}
