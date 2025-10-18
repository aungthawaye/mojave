package io.mojaloop.core.quoting.domain.command;

import io.mojaloop.component.jpa.transaction.TransactionContext;
import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.core.quoting.contract.command.GetQuotesCommand;
import io.mojaloop.core.quoting.domain.QuotingDomainConfiguration;
import io.mojaloop.core.quoting.domain.repository.QuoteRepository;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopCommunicationException;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.component.handy.FspiopUrls;
import io.mojaloop.fspiop.component.handy.PayeeOrServerExceptionResponder;
import io.mojaloop.fspiop.service.api.forwarder.ForwardRequest;
import io.mojaloop.fspiop.service.api.quotes.RespondQuotes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

@Service
public class GetQuotesCommandHandler implements GetQuotesCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostQuotesCommandHandler.class);

    private final ParticipantStore participantStore;

    private final RespondQuotes respondQuotes;

    private final ForwardRequest forwardRequest;

    private final QuoteRepository quoteRepository;

    private final PlatformTransactionManager transactionManager;

    private final QuotingDomainConfiguration.QuoteSettings quoteSettings;

    public GetQuotesCommandHandler(ParticipantStore participantStore,
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

    @Override
    public Output execute(Input input) {

        var udfQuoteId = input.udfQuoteId();

        LOGGER.info("({}) Executing GetQuotesCommandHandler with input: [{}]", udfQuoteId.getId(), input);

        FspCode payerFspCode = null;
        FspData payerFsp = null;
        FspCode payeeFspCode = null;
        FspData payeeFsp = null;

        try {

            payeeFspCode = new FspCode(input.request().payee().fspCode());
            payeeFsp = this.participantStore.getFspData(payeeFspCode);
            LOGGER.info("({}) Found payee FSP: [{}]", udfQuoteId.getId(), payeeFsp);

            payerFspCode = new FspCode(input.request().payer().fspCode());
            payerFsp = this.participantStore.getFspData(payerFspCode);
            LOGGER.info("({}) Found payer FSP: [{}]", udfQuoteId.getId(), payerFsp);

            if (this.quoteSettings.stateful()) {

                TransactionContext.startNew(this.transactionManager, input.udfQuoteId().getId());
                var optQuote = this.quoteRepository.findOne(QuoteRepository.Filters.withUdfQuoteId(input.udfQuoteId()));

                if (optQuote.isEmpty()) {

                    LOGGER.warn("({}) Receiving non-existence Quote.", udfQuoteId.getId());
                    throw new FspiopException(FspiopErrors.QUOTE_ID_NOT_FOUND);
                }

                var quote = optQuote.get();
                LOGGER.info("({}) Found Quote object with UDF Quote ID: [{}] , quote : {}", udfQuoteId.getId(), quote.getId(), quote);

                var quoteIdPutResponse = quote.toFspiopResponse();

                TransactionContext.commit();

                var payerBaseUrl = payerFsp.endpoints().get(EndpointType.PARTIES).baseUrl();
                LOGGER.info("({}) Responding request to payer FSP (Url): [{}]", udfQuoteId.getId(), payerBaseUrl);

                try {

                    this.respondQuotes.putQuotes(new Payer(payeeFspCode.value()), payerBaseUrl, quoteIdPutResponse);
                    LOGGER.info("({}) Done responding request to payer FSP (Url): [{}]", udfQuoteId.getId(), payerBaseUrl);

                } catch (FspiopCommunicationException ignored) {
                    // Do nothing. We are not able to respond to the payer.
                }

            } else {

                var payeeBaseUrl = payeeFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
                LOGGER.info("({}) Forwarding request to payee FSP (Url): [{}]", udfQuoteId.getId(), payeeFsp);

                this.forwardRequest.forward(payeeBaseUrl, input.request());
                LOGGER.info("({}) Done forwarding request to payee FSP (Url): [{}]", udfQuoteId.getId(), payeeFsp);
            }

        } catch (Exception e) {

            LOGGER.error("Exception occurred while executing GetPartiesCommandHandler: ", e);

            if (payerFspCode != null && payerFsp != null) {

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

        LOGGER.info("Returning from GetQuotesCommandHandler successfully.");

        return new Output();
    }

}
