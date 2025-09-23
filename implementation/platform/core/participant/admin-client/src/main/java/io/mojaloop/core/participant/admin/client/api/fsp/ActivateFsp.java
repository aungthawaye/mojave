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
package io.mojaloop.core.participant.admin.client.api.fsp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.participant.admin.client.exception.ParticipantAdminClientException;
import io.mojaloop.core.participant.admin.client.service.ParticipantAdminService;
import io.mojaloop.core.participant.contract.command.fsp.ActivateFspCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ActivateFsp {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivateFsp.class);

    private final ParticipantAdminService.FspCommands fspCommands;

    private final ObjectMapper objectMapper;

    public ActivateFsp(ParticipantAdminService.FspCommands fspCommands, ObjectMapper objectMapper) {

        assert fspCommands != null;
        assert objectMapper != null;

        this.fspCommands = fspCommands;
        this.objectMapper = objectMapper;
    }

    public ActivateFspCommand.Output execute(ActivateFspCommand.Input input) throws ParticipantAdminClientException {

        try {

            return RetrofitService
                       .invoke(this.fspCommands.activateFsp(input), (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper))
                       .body();

        } catch (RetrofitService.InvocationException e) {

            LOGGER.error("Error invoking activateFsp : {}", e.getMessage());

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse(String code, String message)) {

                throw new ParticipantAdminClientException(code, message);
            }

            throw new ParticipantAdminClientException("INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }
}
