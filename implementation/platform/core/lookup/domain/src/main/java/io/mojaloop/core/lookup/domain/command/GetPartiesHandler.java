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

import io.mojaloop.core.common.datatype.enumeration.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.lookup.contract.command.GetParties;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.utility.store.ParticipantStore;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.service.api.forwarder.ForwardRequest;
import io.mojaloop.fspiop.service.api.parties.RespondParties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GetPartiesHandler implements GetParties {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPartiesHandler.class);

    private final ParticipantStore participantStore;

    private final RespondParties respondParties;

    private final ForwardRequest forwardRequest;

    public GetPartiesHandler(ParticipantStore participantStore, RespondParties respondParties, ForwardRequest forwardRequest) {

        assert participantStore != null;
        assert respondParties != null;
        assert forwardRequest != null;

        this.participantStore = participantStore;
        this.respondParties = respondParties;
        this.forwardRequest = forwardRequest;
    }

    @Override
    public Output execute(Input input) {

        try {

            LOGGER.info("Executing GetPartiesHandler with input: [{}]", input);

            var sourceFspCode = new FspCode(input.fspiopHttpRequest().source().sourceFspCode());
            var sourceFsp = this.participantStore.getFspData(sourceFspCode);
            LOGGER.info("Found source FSP: [{}]", sourceFsp);

            var destinationFsp = this.findDestinationFsp(input);

            if (destinationFsp == null) {
                LOGGER.info("Destination FSP is not found in Hub.");
                throw new FspiopException(FspiopErrors.PAYEE_FSP_ID_NOT_FOUND);
            }

            LOGGER.info("Found destination FSP: [{}]", destinationFsp);

            var baseUrl = destinationFsp.endpoints().get(EndpointType.PARTIES).baseUrl();
            LOGGER.info("Forwarding request to destination FSP (Url): [{}]", destinationFsp);

            this.forwardRequest.forward(baseUrl, input.fspiopHttpRequest());
            LOGGER.info("Done forwarding request to destination FSP (Url): [{}]", destinationFsp);

        } catch (FspiopException e) {

            LOGGER.error("FspiopException occurred while executing GetPartiesHandler: [{}]", e.getMessage());
            //this.respondParties.putPartiesError(input.fspiopHttpRequest().destination(),);

        }

        return new Output();
    }

    private FspData findDestinationFsp(Input input) {

        if (input.fspiopHttpRequest().destination().isEmpty()) {

            LOGGER.info("Destination FSP is empty. Use Oracle to find it.");
            var oracle = this.participantStore.getOracleData(input.partyIdType());

            return null;

        } else {

            LOGGER.info("Destination FSP is not empty. Use it.");
            var destinationFspCode = new FspCode(input.fspiopHttpRequest().destination().destinationFspCode());
            return this.participantStore.getFspData(destinationFspCode);
        }

    }

}
