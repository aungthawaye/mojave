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
package org.mojave.rail.fspiop.lookup.domain.command;

import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.common.datatype.enums.participant.EndpointType;
import org.mojave.core.common.datatype.type.participant.FspCode;
import org.mojave.rail.fspiop.lookup.contract.command.PutPartiesErrorCommand;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.participant.store.ParticipantStore;
import org.mojave.rail.fspiop.component.exception.FspiopCommunicationException;
import org.mojave.rail.fspiop.component.type.Payer;
import org.mojave.rail.fspiop.component.handy.FspiopErrorResponder;
import org.mojave.rail.fspiop.component.handy.FspiopUrls;
import org.mojave.rail.fspiop.bootstrap.api.forwarder.ForwardRequest;
import org.mojave.rail.fspiop.bootstrap.api.parties.RespondParties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
public class PutPartiesErrorCommandHandler implements PutPartiesErrorCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        PutPartiesErrorCommandHandler.class);

    private final ParticipantStore participantStore;

    private final RespondParties respondParties;

    private final ForwardRequest forwardRequest;

    public PutPartiesErrorCommandHandler(ParticipantStore participantStore,
                                         RespondParties respondParties,
                                         ForwardRequest forwardRequest) {

        Objects.requireNonNull(participantStore);
        Objects.requireNonNull(respondParties);
        Objects.requireNonNull(forwardRequest);

        this.participantStore = participantStore;
        this.respondParties = respondParties;
        this.forwardRequest = forwardRequest;
    }

    @Override
    public PutPartiesErrorCommand.Output execute(PutPartiesErrorCommand.Input input) {

        LOGGER.info("PutPartiesErrorCommandHandler : input: ({})", ObjectLogger.log(input));

        FspCode payerFspCode = null;
        FspData payerFsp = null;

        try {

            payerFspCode = new FspCode(input.request().payer().fspCode());
            payerFsp = this.participantStore.getFspData(payerFspCode);

            var payerBaseUrl = payerFsp.endpoints().get(EndpointType.PARTIES).baseUrl();
            LOGGER.info("Forwarding request to payer FSP (Url): ({})", payerBaseUrl);

            this.forwardRequest.forward(payerBaseUrl, input.request());
            LOGGER.info("Done forwarding request to payer FSP (Url): ({})", payerBaseUrl);

        } catch (FspiopCommunicationException e) {

            LOGGER.error("Error:", e);

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            if (payerFsp != null) {

                final var sendBackTo = new Payer(payerFspCode.value());
                final var baseUrl = payerFsp.endpoints().get(EndpointType.PARTIES).baseUrl();
                final var url = FspiopUrls.Parties.putPartiesError(
                    baseUrl, input.partyIdType(), input.partyId(), input.subId());

                try {

                    FspiopErrorResponder.toPayer(
                        new Payer(payerFspCode.value()), e,
                        (payer, error) -> this.respondParties.putPartiesError(
                            sendBackTo, url,
                            error));

                } catch (Exception e1) {
                    LOGGER.error("Error:", e1);
                }
            }
        }

        LOGGER.info("PutPartiesErrorCommandHandler : done");
        return new Output();
    }

}
