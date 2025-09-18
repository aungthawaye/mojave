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
package io.mojaloop.core.participant.intercom.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.participant.contract.data.OracleData;
import io.mojaloop.core.participant.intercom.client.exception.ParticipantIntercomClientException;
import io.mojaloop.core.participant.intercom.client.service.ParticipantIntercomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetOracles {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetOracles.class);

    private final ParticipantIntercomService participantIntercomService;

    private final ObjectMapper objectMapper;

    public GetOracles(ParticipantIntercomService participantIntercomService, ObjectMapper objectMapper) {

        assert participantIntercomService != null;
        assert objectMapper != null;

        this.participantIntercomService = participantIntercomService;
        this.objectMapper = objectMapper;
    }

    public List<OracleData> execute() throws ParticipantIntercomClientException {

        try {

            return RetrofitService
                       .invoke(this.participantIntercomService.getOracles(),
                               (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, objectMapper)
                              )
                       .body();

        } catch (RetrofitService.InvocationException e) {

            LOGGER.error("Error invoking getOracles : {}", e.getMessage());

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse(String code, String message)) {

                throw new ParticipantIntercomClientException(code, message);
            }

            throw new ParticipantIntercomClientException("INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

}
