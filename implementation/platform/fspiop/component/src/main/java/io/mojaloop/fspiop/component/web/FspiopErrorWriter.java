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
package io.mojaloop.fspiop.component.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.spec.core.ErrorInformation;
import io.mojaloop.fspiop.spec.core.ErrorInformationResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class FspiopErrorWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspiopErrorWriter.class);

    private final ObjectMapper objectMapper;

    public FspiopErrorWriter(ObjectMapper objectMapper) {

        assert objectMapper != null;

        this.objectMapper = objectMapper;
    }

    public void write(int statusCode, HttpServletResponse response, FspiopException fspiopException) {

        try {

            var errorInformationResponse = new ErrorInformationResponse();

            var errorInformation = new ErrorInformation();
            errorInformation.errorCode(fspiopException.getErrorDefinition().code());
            errorInformation.errorDescription(fspiopException.getMessage());

            errorInformationResponse.errorInformation(errorInformation);

            response.setStatus(statusCode);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write(this.objectMapper.writeValueAsString(errorInformationResponse));

        } catch (Exception e) {

            LOGGER.error("Problem occurred :", e);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String errorCode = FspiopErrors.GENERIC_SERVER_ERROR.code();
            String errorDescription = FspiopErrors.GENERIC_SERVER_ERROR.description();

            var json =
                "{" + "\"errorInformation\": {" + "\"errorCode\": \"" + errorCode + "\"," + "\"errorDescription\": \"" + errorDescription +
                    "\"" + "}" + "}";

            try {
                response.getWriter().write(json);
            } catch (IOException ex) {
                LOGGER.error("Problem occurred :", ex);
                throw new RuntimeException(ex);
            }
        }
    }

}
