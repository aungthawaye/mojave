package io.mojaloop.core.quoting.domain.command;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.component.jpa.transaction.TransactionContext;
import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.identifier.quoting.UdfQuoteId;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.core.quoting.contract.command.PostQuotesCommand;
import io.mojaloop.core.quoting.contract.exception.ExpirationNotInFutureException;
import io.mojaloop.core.quoting.domain.model.Party;
import io.mojaloop.core.quoting.domain.model.Quote;
import io.mojaloop.core.quoting.domain.repository.QuoteRepository;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Payee;
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

@Service
public class PostQuotesCommandHandler implements PostQuotesCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostQuotesCommandHandler.class);

    private final ParticipantStore participantStore;

    private final RespondQuotes respondQuotes;

    private final ForwardRequest forwardRequest;

    private final QuoteRepository quoteRepository;

    private final PlatformTransactionManager transactionManager;

    public PostQuotesCommandHandler(ParticipantStore participantStore,
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

        LOGGER.info("Executing PostQuotesCommandHandler.");

        var udfQuoteId = new UdfQuoteId(input.quotesPostRequest().getQuoteId());
        LOGGER.info("UDF Quote ID: {}", udfQuoteId);

        var payerFspCode = new FspCode(input.request().payer().fspCode());
        var payerFsp = this.participantStore.getFspData(payerFspCode);
        LOGGER.info("({}) Found payer FSP: [{}]", udfQuoteId.getId(), payerFsp);

        try {

            LOGGER.info("({}) Executing PostQuotesCommandHandler with input: [{}]", udfQuoteId.getId(), input);

            var payeeFspCode = new FspCode(input.request().payee().fspCode());
            var payeeFsp = this.participantStore.getFspData(payeeFspCode);

            if (payeeFsp == null) {

                LOGGER.error("({}) Payee FSP is not found in Hub. Send error response to payer FSP.", udfQuoteId.getId());
                throw new FspiopException(FspiopErrors.PAYEE_FSP_ID_NOT_FOUND);
            }

            LOGGER.info("({}) Found payee FSP: [{}]", udfQuoteId.getId(), payeeFsp);

            var postQuotesRequest = input.quotesPostRequest();
            LOGGER.info("({}) postQuotesRequest: [{}]", udfQuoteId.getId(), postQuotesRequest);

            var amount = postQuotesRequest.getAmount();
            var fees = postQuotesRequest.getFees();
            var currency = amount.getCurrency();
            var payer = postQuotesRequest.getPayer().getPartyIdInfo();
            var payee = postQuotesRequest.getPayee().getPartyIdInfo();
            var transactionType = postQuotesRequest.getTransactionType();

            try {

                if (fees.getCurrency() != currency) {

                    throw new FspiopException(FspiopErrors.GENERIC_VALIDATION_ERROR, "The currency of amount and fees must be the same.");
                }

                var payeeBaseUrl = payeeFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
                LOGGER.info("({}) Forwarding request to payee FSP (Url): [{}]", udfQuoteId.getId(), payeeFsp);

                this.forwardRequest.forward(payeeBaseUrl, input.request());
                LOGGER.info("({}) Done forwarding request to payee FSP (Url): [{}]", udfQuoteId.getId(), payeeFsp);

                var quote = new Quote(udfQuoteId,
                                      currency,
                                      new BigDecimal(amount.getAmount()),
                                      new BigDecimal(fees.getAmount()),
                                      postQuotesRequest.getAmountType(),
                                      transactionType.getScenario(),
                                      transactionType.getSubScenario(),
                                      transactionType.getInitiator(),
                                      transactionType.getInitiatorType(),
                                      FspiopDates.fromRequestBody(postQuotesRequest.getExpiration()),
                                      new Party(payer.getPartyIdType(), payer.getPartyIdentifier(), payer.getPartySubIdOrType()),
                                      new Party(payee.getPartyIdType(), payee.getPartyIdentifier(), payee.getPartySubIdOrType()));

                LOGGER.info("({}) Created Quote object with UDF Quote ID: [{}] , quote : {}", udfQuoteId.getId(), udfQuoteId.getId(), quote);

                // I don't use @Transactional and manually control the transaction scope because
                // forwardRequest is the HTTP API call, and it has latency. To avoid holding the
                // connection for a long period, I used manual transaction control.
                TransactionContext.startNew(this.transactionManager, quote.getId().toString());
                this.quoteRepository.save(quote);
                TransactionContext.commit();

                LOGGER.info("({}) Requested quote : quoteId : {}", udfQuoteId.getId(), quote.getId());

            } catch (ExpirationNotInFutureException e) {

                LOGGER.error("({}) Expiration date/time must be in the future.", udfQuoteId.getId());
                throw new FspiopException(FspiopErrors.GENERIC_VALIDATION_ERROR, "Expiration date/time must be in the future.");

            } catch (ParseException e) {

                LOGGER.error("({}) The date/time format of expiration is not valid.", udfQuoteId.getId());
                throw new FspiopException(FspiopErrors.GENERIC_VALIDATION_ERROR, "The date/time format of expiration is not valid.");
            }

        } catch (FspiopException e) {

            LOGGER.error("({}) FspiopException occurred while executing PostQuotesCommandHandler: [{}]", udfQuoteId.getId(), e.getMessage());

            var sendBackTo = new Payer(payerFspCode.value());
            var baseUrl = payerFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
            var url = FspiopUrls.newUrl(baseUrl, input.request().uri() + "/error");

            try {

                this.respondQuotes.putQuotesError(sendBackTo, url, e.toErrorObject());
                LOGGER.info("({}) Done sending error response to payer FSP.", udfQuoteId.getId());
                LOGGER.info("({}) Returning from PostQuotesCommandHandler.", udfQuoteId.getId());

            } catch (FspiopException ignored) {
                LOGGER.error("({}) Something went wrong while sending error response to payer FSP: ", udfQuoteId.getId(), e);
            }

        }

        LOGGER.info("Returning from PostQuotesCommandHandler successfully.");
        return new Output();
    }

}
