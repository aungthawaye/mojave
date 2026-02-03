/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */
package org.mojave.rail.fspiop.quoting.domain.command;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.jpa.transaction.TransactionContext;
import org.mojave.core.common.datatype.enums.Direction;
import org.mojave.core.common.datatype.enums.participant.EndpointType;
import org.mojave.core.common.datatype.type.participant.FspCode;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.participant.store.ParticipantStore;
import org.mojave.rail.fspiop.quoting.contract.command.PutQuotesErrorCommand;
import org.mojave.rail.fspiop.quoting.domain.QuotingDomainConfiguration;
import org.mojave.rail.fspiop.quoting.domain.repository.QuoteRepository;
import org.mojave.rail.fspiop.component.exception.FspiopCommunicationException;
import org.mojave.rail.fspiop.component.type.Payer;
import org.mojave.rail.fspiop.component.handy.FspiopErrorResponder;
import org.mojave.rail.fspiop.component.handy.FspiopUrls;
import org.mojave.rail.fspiop.bootstrap.api.forwarder.ForwardRequest;
import org.mojave.rail.fspiop.bootstrap.api.quotes.RespondQuotes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import java.util.Objects;

@Service
public class PutQuotesErrorCommandHandler implements PutQuotesErrorCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        PutQuotesErrorCommandHandler.class);

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

        Objects.requireNonNull(participantStore);
        Objects.requireNonNull(respondQuotes);
        Objects.requireNonNull(forwardRequest);
        Objects.requireNonNull(quoteRepository);
        Objects.requireNonNull(transactionManager);
        Objects.requireNonNull(quoteSettings);

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

        LOGGER.info("PutQuotesErrorCommandHandler : input: ({})", input);

        var udfQuoteId = input.udfQuoteId();

        FspCode payerFspCode = null;
        FspData payerFsp = null;

        try {

            payerFspCode = new FspCode(input.request().payer().fspCode());
            payerFsp = this.participantStore.getFspData(payerFspCode);

            var error = input.error();

            if (this.quoteSettings.stateful()) {

                TransactionContext.startNew(this.transactionManager, udfQuoteId.getId());
                var optQuote = this.quoteRepository.findOne(
                    QuoteRepository.Filters.withUdfQuoteId(udfQuoteId));

                if (optQuote.isEmpty()) {

                    LOGGER.warn(
                        "Receiving non-existence Quote. Just ignore it. udfQuoteId : ({})",
                        udfQuoteId.getId());

                    return new Output();
                }

                var quote = optQuote.get();

                quote.error(error.getErrorInformation().getErrorDescription());

                if (error.getErrorInformation().getExtensionList() != null &&
                        error.getErrorInformation().getExtensionList().getExtension() != null) {
                    error.getErrorInformation().getExtensionList().getExtension().forEach(ext -> {

                        quote.addExtension(Direction.TO_PAYEE, ext.getKey(), ext.getValue());
                    });
                }

                this.quoteRepository.save(quote);
                TransactionContext.commit();

            }

            var payerBaseUrl = payerFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
            LOGGER.info("Forwarding request to payer FSP (Url): ({})", payerBaseUrl);

            this.forwardRequest.forward(payerBaseUrl, input.request());
            LOGGER.info("Done forwarding request to payer FSP (Url): ({})", payerBaseUrl);

        } catch (FspiopCommunicationException e) {

            LOGGER.error("Error:", e);

        } catch (Exception e) {

            LOGGER.error("Error:", e);

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

        LOGGER.info("PutQuotesErrorCommandHandler : done");

        return new Output();
    }

}
