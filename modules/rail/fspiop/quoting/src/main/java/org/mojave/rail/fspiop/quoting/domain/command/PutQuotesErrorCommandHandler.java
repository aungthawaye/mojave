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

import org.mojave.scheme.rule.enums.participant.EndpointType;
import org.mojave.scheme.rule.type.participant.FspCode;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.participant.store.ParticipantStore;
import org.mojave.rail.fspiop.quoting.contract.command.PutQuotesErrorCommand;
import org.mojave.rail.fspiop.quoting.contract.command.step.UpdateQuotesErrorStep;
import org.mojave.rail.fspiop.quoting.domain.QuotingDomainConfiguration;
import org.mojave.rail.fspiop.quoting.domain.async.producer.UpdateQuotesErrorStepProducer;
import org.mojave.rail.fspiop.component.exception.FspiopCommunicationException;
import org.mojave.rail.fspiop.component.type.Payer;
import org.mojave.rail.fspiop.component.handy.FspiopErrorResponder;
import org.mojave.rail.fspiop.component.handy.FspiopUrls;
import org.mojave.rail.fspiop.service.api.forwarder.ForwardRequest;
import org.mojave.rail.fspiop.service.api.quotes.RespondQuotes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
public class PutQuotesErrorCommandHandler implements PutQuotesErrorCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        PutQuotesErrorCommandHandler.class);

    private final ParticipantStore participantStore;

    private final RespondQuotes respondQuotes;

    private final ForwardRequest forwardRequest;

    private final UpdateQuotesErrorStepProducer updateQuotesErrorStepProducer;

    private final QuotingDomainConfiguration.QuoteSettings quoteSettings;

    public PutQuotesErrorCommandHandler(ParticipantStore participantStore,
                                        RespondQuotes respondQuotes,
                                        ForwardRequest forwardRequest,
                                        UpdateQuotesErrorStepProducer updateQuotesErrorStepProducer,
                                        QuotingDomainConfiguration.QuoteSettings quoteSettings) {

        Objects.requireNonNull(participantStore);
        Objects.requireNonNull(respondQuotes);
        Objects.requireNonNull(forwardRequest);
        Objects.requireNonNull(updateQuotesErrorStepProducer);
        Objects.requireNonNull(quoteSettings);

        this.participantStore = participantStore;
        this.respondQuotes = respondQuotes;
        this.forwardRequest = forwardRequest;
        this.updateQuotesErrorStepProducer = updateQuotesErrorStepProducer;
        this.quoteSettings = quoteSettings;
    }

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

                this.updateQuotesErrorStepProducer.publish(
                    new UpdateQuotesErrorStep.Input(
                        udfQuoteId,
                        error.getErrorInformation().getErrorDescription(),
                        error.getErrorInformation().getExtensionList()));
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
