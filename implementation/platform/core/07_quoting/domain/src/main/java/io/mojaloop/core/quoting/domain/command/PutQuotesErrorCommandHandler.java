/*-
 * ================================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */

package io.mojaloop.core.quoting.domain.command;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.component.jpa.transaction.TransactionContext;
import io.mojaloop.core.common.datatype.enums.Direction;
import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.core.quoting.contract.command.PutQuotesErrorCommand;
import io.mojaloop.core.quoting.domain.QuotingDomainConfiguration;
import io.mojaloop.core.quoting.domain.repository.QuoteRepository;
import io.mojaloop.fspiop.common.exception.FspiopCommunicationException;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.component.handy.FspiopErrorResponder;
import io.mojaloop.fspiop.component.handy.FspiopUrls;
import io.mojaloop.fspiop.service.api.forwarder.ForwardRequest;
import io.mojaloop.fspiop.service.api.quotes.RespondQuotes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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

    @Write
    @Override
    public Output execute(Input input) {

        var udfQuoteId = input.udfQuoteId();

        MDC.put("requestId", udfQuoteId.getId());

        LOGGER.info("({}) Executing PutQuotesErrorCommandHandler with input: [{}].", udfQuoteId.getId(), input);

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
                        quote.addExtension(Direction.TO_PAYEE, ext.getKey(), ext.getValue());
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

        } catch (FspiopCommunicationException e) {

            LOGGER.error("({}) (FspiopCommunicationException) Exception occurred while executing PutQuotesErrorCommandHandler: [{}]", udfQuoteId.getId(), e.getMessage());

        } catch (Exception e) {

            LOGGER.error("({}) (Exception) Exception occurred while executing PutQuotesErrorCommandHandler: [{}]", udfQuoteId.getId(), e.getMessage());

            if (payerFsp != null) {

                final var sendBackTo = new Payer(payerFspCode.value());
                final var baseUrl = payerFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
                final var url = FspiopUrls.newUrl(baseUrl, input.request().uri() + "/error");

                try {

                    FspiopErrorResponder.toPayer(new Payer(payerFspCode.value()), e, (payer, error) -> this.respondQuotes.putQuotesError(sendBackTo, url, error));

                } catch (Throwable ignored) {
                    LOGGER.error("Something went wrong while sending error response to payer FSP: ", e);
                }
            }
        }

        LOGGER.info("Returning from PutQuotesErrorCommandHandler successfully.");

        MDC.remove("requestId");

        return new Output();
    }

}
