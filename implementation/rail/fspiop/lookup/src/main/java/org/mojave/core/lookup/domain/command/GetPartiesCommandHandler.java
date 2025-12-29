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
package org.mojave.core.lookup.domain.command;

import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.common.datatype.enums.fspiop.EndpointType;
import org.mojave.core.common.datatype.type.participant.FspCode;
import org.mojave.core.lookup.contract.command.GetPartiesCommand;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.participant.store.ParticipantStore;
import org.mojave.fspiop.component.type.Payer;
import org.mojave.fspiop.component.handy.FspiopErrorResponder;
import org.mojave.fspiop.component.handy.FspiopUrls;
import org.mojave.fspiop.service.api.forwarder.ForwardRequest;
import org.mojave.fspiop.service.api.parties.RespondParties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GetPartiesCommandHandler implements GetPartiesCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPartiesCommandHandler.class);

    private final ParticipantStore participantStore;

    private final RespondParties respondParties;

    private final ForwardRequest forwardRequest;

    public GetPartiesCommandHandler(ParticipantStore participantStore,
                                    RespondParties respondParties,
                                    ForwardRequest forwardRequest) {

        assert participantStore != null;
        assert respondParties != null;
        assert forwardRequest != null;

        this.participantStore = participantStore;
        this.respondParties = respondParties;
        this.forwardRequest = forwardRequest;
    }

    @Override
    public Output execute(Input input) {

        LOGGER.info("GetPartiesCommandHandler : input: ({})", ObjectLogger.log(input));

        FspCode payerFspCode = null;
        FspData payerFsp = null;
        FspCode payeeFspCode = null;
        FspData payeeFsp = null;

        try {

            payerFspCode = new FspCode(input.request().payer().fspCode());
            payerFsp = this.participantStore.getFspData(payerFspCode);

            payeeFspCode = new FspCode(input.request().payee().fspCode());
            payeeFsp = this.participantStore.getFspData(payeeFspCode);

            var payeeBaseUrl = payeeFsp.endpoints().get(EndpointType.PARTIES).baseUrl();
            LOGGER.info("Forwarding request to payee FSP (Url): ({})", payeeBaseUrl);

            this.forwardRequest.forward(payeeBaseUrl, input.request());
            LOGGER.info("Done forwarding request to payee FSP (Url): ({})", payeeBaseUrl);

        } catch (Exception e) {

            LOGGER.error("Error: ", e);

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
                    LOGGER.error("Error: ", e1);
                }
            }

        }

        LOGGER.info("GetPartiesCommandHandler : done");
        return new Output();
    }

}
