package io.mojaloop.core.quoting.domain.command;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.component.jpa.transaction.TransactionContext;
import io.mojaloop.core.common.datatype.enums.Direction;
import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.identifier.quoting.UdfQuoteId;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.core.quoting.contract.command.PostQuotesCommand;
import io.mojaloop.core.quoting.contract.exception.ExpirationNotInFutureException;
import io.mojaloop.core.quoting.domain.QuotingDomainConfiguration;
import io.mojaloop.core.quoting.domain.model.Party;
import io.mojaloop.core.quoting.domain.model.Quote;
import io.mojaloop.core.quoting.domain.repository.QuoteRepository;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.component.handy.FspiopDates;
import io.mojaloop.fspiop.component.handy.FspiopUrls;
import io.mojaloop.fspiop.component.handy.PayeeOrServerExceptionResponder;
import io.mojaloop.fspiop.service.api.forwarder.ForwardRequest;
import io.mojaloop.fspiop.service.api.quotes.RespondQuotes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class PostQuotesCommandHandler implements PostQuotesCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostQuotesCommandHandler.class);

    private final ParticipantStore participantStore;

    private final RespondQuotes respondQuotes;

    private final ForwardRequest forwardRequest;

    private final QuoteRepository quoteRepository;

    private final PlatformTransactionManager transactionManager;

    private final QuotingDomainConfiguration.QuoteSettings quoteSettings;

    public PostQuotesCommandHandler(ParticipantStore participantStore,
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

        var udfQuoteId = new UdfQuoteId(input.quotesPostRequest().getQuoteId());

        LOGGER.info("({}) Executing PostQuotesCommandHandler with input: [{}]", udfQuoteId.getId(), input);

        FspCode payerFspCode = null;
        FspData payerFsp = null;
        FspCode payeeFspCode = null;
        FspData payeeFsp = null;

        try {

            payerFspCode = new FspCode(input.request().payer().fspCode());
            payerFsp = this.participantStore.getFspData(payerFspCode);
            LOGGER.info("({}) Found payer FSP: [{}]", udfQuoteId.getId(), payerFsp);

            payeeFspCode = new FspCode(input.request().payee().fspCode());
            payeeFsp = this.participantStore.getFspData(payeeFspCode);
            LOGGER.info("({}) Found payee FSP: [{}]", udfQuoteId.getId(), payeeFsp);

            var postQuotesRequest = input.quotesPostRequest();
            LOGGER.info("({}) postQuotesRequest: [{}]", udfQuoteId.getId(), postQuotesRequest);

            var amount = postQuotesRequest.getAmount();
            var fees = postQuotesRequest.getFees();
            var currency = amount.getCurrency();
            var payer = postQuotesRequest.getPayer().getPartyIdInfo();
            var payee = postQuotesRequest.getPayee().getPartyIdInfo();
            var transactionType = postQuotesRequest.getTransactionType();
            var expiration = postQuotesRequest.getExpiration();

            Instant expireAt = null;

            if (fees != null && fees.getCurrency() != currency) {

                throw new FspiopException(FspiopErrors.GENERIC_VALIDATION_ERROR, "The currency of amount and fees must be the same.");
            }

            if (expiration != null) {

                expireAt = FspiopDates.fromRequestBody(expiration);
            }

            if (this.quoteSettings.stateful()) {

                try {

                    var quote = new Quote(payerFsp.fspId(),
                                          payeeFsp.fspId(),
                                          udfQuoteId,
                                          currency,
                                          new BigDecimal(amount.getAmount()),
                                          fees != null ? new BigDecimal(fees.getAmount()) : null,
                                          postQuotesRequest.getAmountType(),
                                          transactionType.getScenario(),
                                          transactionType.getSubScenario(),
                                          transactionType.getInitiator(),
                                          transactionType.getInitiatorType(),
                                          expireAt,
                                          new Party(payer.getPartyIdType(), payer.getPartyIdentifier(), payer.getPartySubIdOrType()),
                                          new Party(payee.getPartyIdType(), payee.getPartyIdentifier(), payee.getPartySubIdOrType()));

                    if (postQuotesRequest.getExtensionList() != null && postQuotesRequest.getExtensionList().getExtension() != null) {
                        postQuotesRequest.getExtensionList().getExtension().forEach(extension -> {
                            LOGGER.debug("({}) Extension found: {}", udfQuoteId.getId(), extension);
                            quote.addExtension(Direction.OUTBOUND, extension.getKey(), extension.getValue());
                        });
                    }

                    LOGGER.info("({}) Created Quote object with UDF Quote ID: [{}] , quote : {}", udfQuoteId.getId(), udfQuoteId.getId(), quote);

                    // I don't use @Transactional and manually control the transaction scope because
                    // forwardRequest is the HTTP API call, and it has latency. To avoid holding the
                    // connection for a long period, I used manual transaction control.
                    TransactionContext.startNew(this.transactionManager, quote.getId().toString());
                    this.quoteRepository.save(quote);
                    TransactionContext.commit();

                    LOGGER.info("({}) Requested quote : quoteId : {}", udfQuoteId.getId(), quote.getId());

                } catch (ExpirationNotInFutureException e) {

                    LOGGER.error("({}) The requested quote has expired.", udfQuoteId.getId());
                    LOGGER.info("({}) Returning from PostQuotesCommandHandler (quote expired).", udfQuoteId.getId());

                    throw new FspiopException(FspiopErrors.QUOTE_EXPIRED, "The quote has expired. The expiration is : " + expiration);

                }
            }

            var payeeBaseUrl = payeeFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
            LOGGER.info("({}) Forwarding request to payee FSP (Url): [{}]", udfQuoteId.getId(), payeeFsp);

            this.forwardRequest.forward(payeeBaseUrl, input.request());
            LOGGER.info("({}) Done forwarding request to payee FSP (Url): [{}]", udfQuoteId.getId(), payeeFsp);

        } catch (Exception e) {

            LOGGER.error("Exception occurred while executing PostQuotesCommandHandler: [{}]", e.getMessage());

            if (payerFsp != null) {

                final var sendBackTo = new Payer(payerFspCode.value());
                final var baseUrl = payerFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
                final var url = FspiopUrls.newUrl(baseUrl, input.request().uri() + "/error");

                try {

                    PayeeOrServerExceptionResponder.respond(new Payer(payerFspCode.value()), e, (payer, error) -> this.respondQuotes.putQuotesError(sendBackTo, url, error));

                } catch (Throwable ignored) {
                    LOGGER.error("Something went wrong while sending error response to payer FSP: ", e);
                }
            }
        }

        LOGGER.info("Returning from PostQuotesCommandHandler successfully.");
        return new Output();
    }

}
