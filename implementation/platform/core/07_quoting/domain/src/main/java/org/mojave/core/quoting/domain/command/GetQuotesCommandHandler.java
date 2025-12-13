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
package org.mojave.core.quoting.domain.command;

import org.mojave.component.jpa.routing.annotation.Read;
import org.mojave.component.jpa.transaction.TransactionContext;
import org.mojave.core.common.datatype.enums.fspiop.EndpointType;
import org.mojave.core.common.datatype.type.participant.FspCode;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.participant.store.ParticipantStore;
import org.mojave.core.quoting.contract.command.GetQuotesCommand;
import org.mojave.core.quoting.domain.QuotingDomainConfiguration;
import org.mojave.core.quoting.domain.repository.QuoteRepository;
import org.mojave.fspiop.component.error.FspiopErrors;
import org.mojave.fspiop.component.exception.FspiopCommunicationException;
import org.mojave.fspiop.component.exception.FspiopException;
import org.mojave.fspiop.component.type.Payer;
import org.mojave.fspiop.component.handy.FspiopErrorResponder;
import org.mojave.fspiop.component.handy.FspiopUrls;
import org.mojave.fspiop.service.api.forwarder.ForwardRequest;
import org.mojave.fspiop.service.api.quotes.RespondQuotes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

@Service
public class GetQuotesCommandHandler implements GetQuotesCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetQuotesCommandHandler.class);

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
    @Read
    public Output execute(Input input) {

        var udfQuoteId = input.udfQuoteId();

        LOGGER.info("GetQuotesCommandHandler : input: ({})", input);

        FspCode payerFspCode = null;
        FspData payerFsp = null;
        FspCode payeeFspCode = null;
        FspData payeeFsp = null;

        try {

            payeeFspCode = new FspCode(input.request().payee().fspCode());
            payeeFsp = this.participantStore.getFspData(payeeFspCode);

            payerFspCode = new FspCode(input.request().payer().fspCode());
            payerFsp = this.participantStore.getFspData(payerFspCode);

            if (this.quoteSettings.stateful()) {

                TransactionContext.startNew(this.transactionManager, input.udfQuoteId().getId());
                var optQuote = this.quoteRepository.findOne(
                    QuoteRepository.Filters.withUdfQuoteId(input.udfQuoteId()));

                if (optQuote.isEmpty()) {

                    LOGGER.warn("Receiving non-existence Quote.");
                    throw new FspiopException(FspiopErrors.QUOTE_ID_NOT_FOUND);
                }

                var quote = optQuote.get();

                var quoteIdPutResponse = quote.toFspiopResponse();

                TransactionContext.commit();

                var payerBaseUrl = payerFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
                var url = FspiopUrls.newUrl(payerBaseUrl, input.request().uri());
                LOGGER.info("Responding request to payer FSP (Url): ({})", url);

                try {

                    this.respondQuotes.putQuotes(
                        new Payer(payeeFspCode.value()), url, quoteIdPutResponse);
                    LOGGER.info("Done responding request to payer FSP (Url): ({})", url);

                } catch (FspiopCommunicationException ignored) {
                    // Do nothing. We are not able to respond to the payer.
                }

            } else {

                var payeeBaseUrl = payeeFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
                LOGGER.info("Forwarding request to payee FSP (Url): ({})", payeeBaseUrl);

                this.forwardRequest.forward(payeeBaseUrl, input.request());
                LOGGER.info("Done forwarding request to payee FSP (Url): ({})", payeeBaseUrl);
            }

        } catch (Exception e) {

            LOGGER.error("Error: ", e);

            if (payerFsp != null) {

                final var sendBackTo = new Payer(payerFspCode.value());
                final var baseUrl = payerFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
                final var url = FspiopUrls.newUrl(baseUrl, input.request().uri() + "/error");

                try {

                    FspiopErrorResponder.toPayer(
                        new Payer(payerFspCode.value()), e,
                        (payer, error) -> this.respondQuotes.putQuotesError(
                            sendBackTo, url,
                            error));

                } catch (Exception e1) {
                    LOGGER.error("Error:", e1);
                }
            }
        }

        LOGGER.info("GetQuotesCommandHandler : done");

        return new Output();
    }

}
