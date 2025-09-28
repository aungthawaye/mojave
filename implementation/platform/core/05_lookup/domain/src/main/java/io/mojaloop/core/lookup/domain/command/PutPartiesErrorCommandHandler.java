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
import io.mojaloop.core.lookup.contract.command.PutPartiesErrorCommand;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Destination;
import io.mojaloop.fspiop.component.handy.FspiopUrls;
import io.mojaloop.fspiop.service.api.forwarder.ForwardRequest;
import io.mojaloop.fspiop.service.api.parties.RespondParties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PutPartiesErrorCommandHandler implements PutPartiesErrorCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutPartiesErrorCommandHandler.class);

    private final ParticipantStore participantStore;

    private final RespondParties respondParties;

    private final ForwardRequest forwardRequest;

    public PutPartiesErrorCommandHandler(ParticipantStore participantStore, RespondParties respondParties, ForwardRequest forwardRequest) {

        assert participantStore != null;
        assert respondParties != null;
        assert forwardRequest != null;

        this.participantStore = participantStore;
        this.respondParties = respondParties;
        this.forwardRequest = forwardRequest;
    }

    @Override
    public PutPartiesErrorCommand.Output execute(PutPartiesErrorCommand.Input input) {

        LOGGER.info("Executing PutPartiesErrorCommandHandler.");

        var sourceFspCode = new FspCode(input.request().source().sourceFspCode());
        var sourceFsp = this.participantStore.getFspData(sourceFspCode);
        LOGGER.info("Found source FSP: [{}]", sourceFsp);

        var destinationFspCode = new FspCode(input.request().destination().destinationFspCode());
        var destinationFsp = this.participantStore.getFspData(destinationFspCode);
        LOGGER.info("Found destination FSP: [{}]", destinationFsp);

        try {

            var destinationBaseUrl = destinationFsp.endpoints().get(EndpointType.PARTIES).baseUrl();
            LOGGER.info("Forwarding request to destination FSP (Url): [{}]", destinationFsp);

            this.forwardRequest.forward(destinationBaseUrl, input.request());
            LOGGER.info("Done forwarding request to destination FSP (Url): [{}]", destinationFsp);

        } catch (FspiopException e) {

            LOGGER.error("FspiopException occurred while executing PutPartiesErrorCommandHandler: [{}]", e.getMessage());

            var sendBackTo = new Destination(sourceFspCode.value());
            var baseUrl = sourceFsp.endpoints().get(EndpointType.PARTIES).baseUrl();
            var url = FspiopUrls.newUrl(baseUrl, input.request().uri() + "/error");

            try {

                this.respondParties.putPartiesError(sendBackTo, url, e.toErrorObject());
                LOGGER.info("Done sending error response to source FSP.");
                LOGGER.info("Returning from PutPartiesErrorCommandHandler.");

            } catch (FspiopException ignored) {
                LOGGER.error("Something went wrong while sending error response to source FSP: ", e);
            }

        }

        LOGGER.info("Returning from PutPartiesErrorCommandHandler.");
        return new Output();
    }

}
