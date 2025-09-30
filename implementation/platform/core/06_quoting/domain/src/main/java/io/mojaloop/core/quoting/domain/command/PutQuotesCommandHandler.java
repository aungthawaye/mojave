package io.mojaloop.core.quoting.domain.command;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.component.jpa.transaction.TransactionContext;
import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.core.quoting.contract.command.PutQuotesCommand;
import io.mojaloop.core.quoting.contract.exception.ReceivingAmountMismatchException;
import io.mojaloop.core.quoting.contract.exception.TransferAmountMismatchException;
import io.mojaloop.core.quoting.domain.repository.QuoteRepository;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Destination;
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

    public PutQuotesCommandHandler(ParticipantStore participantStore,
                                   RespondQuotes respondQuotes,
                                   ForwardRequest forwardRequest,
                                   QuoteRepository quoteRepository,
                                   PlatformTransactionManager transactionManager) {

        assert participantStore != null;
        assert respondQuotes != null;
        assert forwardRequest != null;
        assert quoteRepository != null;
        assert transactionManager != null;

        this.participantStore = participantStore;
        this.respondQuotes = respondQuotes;
        this.forwardRequest = forwardRequest;
        this.quoteRepository = quoteRepository;
        this.transactionManager = transactionManager;
    }

    @Write
    @Override
    public Output execute(Input input) {

        LOGGER.info("Executing PutQuotesCommandHandler.");

        var udfQuoteId = input.udfQuoteId();
        LOGGER.info("UDF Quote ID: {}", udfQuoteId);

        var sourceFspCode = new FspCode(input.request().source().sourceFspCode());
        var sourceFsp = this.participantStore.getFspData(sourceFspCode);
        LOGGER.info("({}) Found source FSP: [{}]", udfQuoteId.getId(), sourceFsp);

        try {

            LOGGER.info("({}) Executing PostQuotesCommandHandler with input: [{}]", udfQuoteId.getId(), input);

            var destinationFspCode = new FspCode(input.request().destination().destinationFspCode());
            var destinationFsp = this.participantStore.getFspData(destinationFspCode);

            if (destinationFsp == null) {

                LOGGER.error("({}) Destination FSP is not found in Hub. Send error response to source FSP.", udfQuoteId.getId());
                throw new FspiopException(FspiopErrors.PAYEE_FSP_ID_NOT_FOUND);
            }

            LOGGER.info("({}) Found destination FSP: [{}]", udfQuoteId.getId(), destinationFsp);

            var quoteIdPutResponse = input.quotesIDPutResponse();
            LOGGER.info("({}) quotesIDPutResponse: [{}]", udfQuoteId.getId(), quoteIdPutResponse);

            TransactionContext.startNew(this.transactionManager, udfQuoteId.getId());
            var optQuote = this.quoteRepository.findOne(QuoteRepository.Filters.withUdfQuoteId(udfQuoteId));

            if (optQuote.isEmpty()) {

                LOGGER.warn("({}) Receiving non-existence Quote. Just ignore it.", udfQuoteId.getId());
                LOGGER.info("({}) Returning from PutQuotesCommandHandler (non-existence Quote).", udfQuoteId.getId());

                return new Output();
            }

            var quote = optQuote.get();
            LOGGER.info("({}) Retrieved Quote object with UDF Quote ID: [{}] , quote : {}", udfQuoteId.getId(), udfQuoteId.getId(), quote);

            Instant responseExpiration;

            try {

                responseExpiration = FspiopDates.fromRequestBody(quoteIdPutResponse.getExpiration());

                if (responseExpiration.isBefore(Instant.now())) {

                    LOGGER.error("({}) The responded quote has expired.", udfQuoteId.getId());
                    LOGGER.info("({}) Returning from PutQuotesCommandHandler (quote expired).", udfQuoteId.getId());

                    quote.error("The responded quote has expired. Response expiration : " + responseExpiration.getEpochSecond());
                    this.quoteRepository.save(quote);
                    TransactionContext.commit();

                    return new Output();
                }

            } catch (ParseException e) {

                LOGGER.error("({}) Wrong expiration format.", udfQuoteId.getId());
                LOGGER.info("({}) Returning from PutQuotesCommandHandler (wrong expiration).", udfQuoteId.getId());

                quote.error("Wrong expiration format. Response expiration format : " + quoteIdPutResponse.getExpiration());
                this.quoteRepository.save(quote);
                TransactionContext.commit();

                return new Output();
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

                return new Output();
            }

            var transferAmount = new BigDecimal(quoteIdPutResponse.getTransferAmount().getAmount());
            var payeeFspFee = new BigDecimal(quoteIdPutResponse.getPayeeFspFee().getAmount());
            var payeeFspCommission = new BigDecimal(quoteIdPutResponse.getPayeeFspCommission().getAmount());
            var payeeReceiveAmount = new BigDecimal(quoteIdPutResponse.getPayeeReceiveAmount().getAmount());

            LOGGER.info("({}) Quote responded : transferAmount : [{}], payeeFspFee : [{}], payeeFspCommission : [{}], payeeReceiveAmount : [{}]",
                        udfQuoteId.getId(),
                        transferAmount,
                        payeeFspFee,
                        payeeFspCommission,
                        payeeReceiveAmount);

            try {

                quote.responded(responseExpiration, transferAmount, payeeFspFee, payeeFspCommission, payeeReceiveAmount);
                this.quoteRepository.save(quote);
                TransactionContext.commit();

            } catch (TransferAmountMismatchException e) {

                LOGGER.error("({}) Transfer amount mismatch.", udfQuoteId.getId(), e);
                quote.error(e.getMessage());
                this.quoteRepository.save(quote);
                TransactionContext.commit();

                return new Output();

            } catch (ReceivingAmountMismatchException e) {

                LOGGER.error("({}) Receiving amount mismatch.", udfQuoteId.getId(), e);
                quote.error(e.getMessage());
                this.quoteRepository.save(quote);
                TransactionContext.commit();

                return new Output();
            }

            var destinationBaseUrl = destinationFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
            LOGGER.info("({}) Forwarding request to destination FSP (Url): [{}]", udfQuoteId.getId(), destinationFsp);

            this.forwardRequest.forward(destinationBaseUrl, input.request());
            LOGGER.info("({}) Done forwarding request to destination FSP (Url): [{}]", udfQuoteId.getId(), destinationFsp);
            LOGGER.info("({}) Responded quote : quoteId : {}", udfQuoteId.getId(), quote.getId());

        } catch (FspiopException e) {

            LOGGER.error("({}) FspiopException occurred while executing PostQuotesCommandHandler: [{}]", udfQuoteId.getId(), e.getMessage());

            var sendBackTo = new Destination(sourceFspCode.value());
            var baseUrl = sourceFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
            var url = FspiopUrls.newUrl(baseUrl, input.request().uri() + "/error");

            try {

                this.respondQuotes.putQuotesError(sendBackTo, url, e.toErrorObject());
                LOGGER.info("({}) Done sending error response to source FSP.", udfQuoteId.getId());
                LOGGER.info("({}) Returning from PutQuotesCommandHandler.", udfQuoteId.getId());

            } catch (FspiopException ignored) {
                LOGGER.error("({}) Something went wrong while sending error response to source FSP: ", udfQuoteId.getId(), e);
            }

        }

        LOGGER.info("Returning from PutQuotesCommandHandler successfully.");
        return new Output();
    }

}
