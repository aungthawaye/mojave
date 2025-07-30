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

import io.mojaloop.core.common.datatype.enumeration.ActivationStatus;
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.lookup.contract.command.GetParties;
import io.mojaloop.core.participant.utility.store.ParticipantStore;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.service.api.parties.RespondParties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GetPartiesHandler implements GetParties {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPartiesHandler.class);

    private final ParticipantStore participantStore;

    private final RespondParties respondParties;

    public GetPartiesHandler(ParticipantStore participantStore, RespondParties respondParties) {

        assert participantStore != null;
        assert respondParties != null;

        this.participantStore = participantStore;
        this.respondParties = respondParties;
    }

    @Override
    public Output execute(Input input) throws FspiopException {

        LOGGER.info("Executing GetPartiesHandler with input: [{}]", input);

        var sourceFsp = this.participantStore.getFspData(new FspCode(input.source().sourceFspCode()));
        LOGGER.info("Found source FSP: [{}]", sourceFsp);

        return null;
    }

}
