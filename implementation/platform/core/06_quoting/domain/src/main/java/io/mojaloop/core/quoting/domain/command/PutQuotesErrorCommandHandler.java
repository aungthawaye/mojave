package io.mojaloop.core.quoting.domain.command;

import io.mojaloop.component.jpa.transaction.TransactionContext;
import io.mojaloop.core.common.datatype.enums.Direction;
import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.core.quoting.contract.command.PutQuotesErrorCommand;
import io.mojaloop.core.quoting.domain.QuotingDomainConfiguration;
import io.mojaloop.core.quoting.domain.repository.QuoteRepository;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.service.api.forwarder.ForwardRequest;
import io.mojaloop.fspiop.service.api.quotes.RespondQuotes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

@Service
public class PutQuotesErrorCommandHandler implements PutQuotesErrorCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutQuotesErrorCommandHandler.class);

    private final ParticipantStore participantStore;

    private final RespondQuotes respondQuotes;

    private final ForwardRequest forwardRequest;

    private final QuoteRepository quoteRepository;

    private final PlatformTransactionManager transactionManager;

    private final QuotingDomainConfiguration.QuoteSettings quoteSettings;

    public PutQuotesErrorCommandHandler(ParticipantStore participantStore,
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
        LOGGER.info("({}) Executing PutQuotesErrorCommandHandler with input: [{}].", udfQuoteId.getId(), input);

        var payeeFspCode = new FspCode(input.request().payee().fspCode());
        var payeeFsp = this.participantStore.getFspData(payeeFspCode);
        LOGGER.info("({}) Found payee FSP: [{}]", udfQuoteId.getId(), payeeFsp);

        var payerFspCode = new FspCode(input.request().payer().fspCode());
        var payerFsp = this.participantStore.getFspData(payerFspCode);
        LOGGER.info("({}) Found payer FSP: [{}]", udfQuoteId.getId(), payerFsp);

        try {

            var error = input.error();
            LOGGER.info("({}) error: [{}]", udfQuoteId.getId(), error);

            if (this.quoteSettings.stateful()) {

                TransactionContext.startNew(this.transactionManager, udfQuoteId.getId());
                var optQuote = this.quoteRepository.findOne(QuoteRepository.Filters.withUdfQuoteId(udfQuoteId));

                if (optQuote.isEmpty()) {

                    LOGGER.warn("({}) Receiving non-existence Quote. Just ignore it.", udfQuoteId.getId());
                    LOGGER.info("({}) Returning from PutQuotesErrorCommandHandler (non-existence Quote).", udfQuoteId.getId());

                    return new Output();
                }

                var quote = optQuote.get();
                LOGGER.info("({}) Retrieved Quote object with UDF Quote ID: [{}] , quote : {}", udfQuoteId.getId(), udfQuoteId.getId(), quote);

                quote.error(error.getErrorInformation().getErrorDescription());

                if (error.getErrorInformation().getExtensionList() != null && error.getErrorInformation().getExtensionList().getExtension() != null) {
                    error.getErrorInformation().getExtensionList().getExtension().forEach(ext -> {
                        LOGGER.debug("({}) Extension found: {}", udfQuoteId.getId(), ext);
                        quote.addExtension(Direction.INBOUND, ext.getKey(), ext.getValue());
                    });
                }

                this.quoteRepository.save(quote);
                TransactionContext.commit();
                LOGGER.info("({}) Quote error set. Error : [{}]", udfQuoteId.getId(), error.getErrorInformation().getErrorDescription());

            }

            var payerBaseUrl = payerFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
            LOGGER.info("({}) Forwarding request to payer FSP (Url): [{}]", udfQuoteId.getId(), payerFsp);

            this.forwardRequest.forward(payerBaseUrl, input.request());
            LOGGER.info("({}) Done forwarding request to payer FSP (Url): [{}]", udfQuoteId.getId(), payerFsp);

        } catch (FspiopException e) {

            LOGGER.error("FspiopException occurred while executing PutQuotesErrorCommandHandler: [{}]", e.getMessage());
            LOGGER.error("Ignore sending error response back to Payee.");

            // For PUT calls, we must not send back an error to the Payee.
            // Here, Payee side responded with PUT, but Hub cannot forward the request to Payer due to some error.
            // But Hub won't respond with an error to the Payee.

            try {

                var errorInformationObject = FspiopErrors.GENERIC_PAYEE_ERROR.toErrorObject();
                this.respondQuotes.putQuotesError(new Payer(payerFspCode.value()), payerFsp.endpoints().get(EndpointType.QUOTES).baseUrl(), errorInformationObject);

            } catch (FspiopException ex) {
                // Do nothing. We are not interested in the response from Payer.
                LOGGER.error("FspiopException occurred while sending error response to Payer FSP: ", ex);
            }

        } catch (Exception e) {

            LOGGER.error("Exception occurred while executing PutQuotesCommandHandler: [{}]", e.getMessage());

            try {

                var errorInformationObject = FspiopErrors.GENERIC_SERVER_ERROR.toErrorObject();
                this.respondQuotes.putQuotesError(new Payer(payerFspCode.value()), payerFsp.endpoints().get(EndpointType.QUOTES).baseUrl(), errorInformationObject);

            } catch (FspiopException ex) {
                // Do nothing. We are not interested in the response from Payer.
                LOGGER.error("FspiopException occurred while sending error response to Payer FSP: ", ex);
            }
        }

        LOGGER.info("Returning from PutQuotesErrorCommandHandler successfully.");
        return new Output();
    }

}
