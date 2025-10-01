/*-
 * ================================================================================
 * Mojaloop OSS
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

package io.mojaloop.core.lookup.domain.command;

import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.lookup.contract.command.PutPartiesCommand;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.service.api.forwarder.ForwardRequest;
import io.mojaloop.fspiop.service.api.parties.RespondParties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PutPartiesCommandHandler implements PutPartiesCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutPartiesCommandHandler.class);

    private final ParticipantStore participantStore;

    private final RespondParties respondParties;

    private final ForwardRequest forwardRequest;

    public PutPartiesCommandHandler(ParticipantStore participantStore, RespondParties respondParties, ForwardRequest forwardRequest) {

        assert participantStore != null;
        assert respondParties != null;
        assert forwardRequest != null;

        this.participantStore = participantStore;
        this.respondParties = respondParties;
        this.forwardRequest = forwardRequest;
    }

    @Override
    public Output execute(Input input) {

        LOGGER.info("Executing PutPartiesCommandHandler with input: [{}].", input);

        var payerFspCode = new FspCode(input.request().payer().fspCode());
        var payerFsp = this.participantStore.getFspData(payerFspCode);
        LOGGER.info("Found payer FSP: [{}]", payerFsp);

        var payeeFspCode = new FspCode(input.request().payee().fspCode());
        var payeeFsp = this.participantStore.getFspData(payeeFspCode);
        LOGGER.info("Found payee FSP: [{}]", payeeFsp);

        try {

            var payerBaseUrl = payerFsp.endpoints().get(EndpointType.PARTIES).baseUrl();
            LOGGER.info("Forwarding request to payer FSP (Url): [{}]", payerFsp);

            this.forwardRequest.forward(payerBaseUrl, input.request());
            LOGGER.info("Done forwarding request to payer FSP (Url): [{}]", payerFsp);

        } catch (FspiopException e) {

            LOGGER.error("FspiopException occurred while executing PutPartiesCommandHandler: [{}]", e.getMessage());
            LOGGER.error("Ignore sending error response back to Payee.");

            // For PUT calls, we must not send back an error to the Payee.
            // Here, Payee side responded with PUT, but Hub cannot forward the request to Payer due to some error.
            // But Hub won't respond with an error to the Payee.

        } catch (Exception e) {

            LOGGER.error("Exception occurred while executing PutPartiesCommandHandler: ", e);

        }

        LOGGER.info("Returning from PutPartiesCommandHandler.");
        return new Output();
    }

}
