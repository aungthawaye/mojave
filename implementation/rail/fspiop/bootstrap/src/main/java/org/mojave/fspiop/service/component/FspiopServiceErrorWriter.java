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
package org.mojave.fspiop.service.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import org.mojave.component.web.spring.security.AuthenticationErrorWriter;
import org.mojave.component.web.spring.security.AuthenticationFailureException;
import org.mojave.fspiop.component.error.FspiopErrors;
import org.mojave.fspiop.component.exception.FspiopException;
import org.mojave.scheme.fspiop.core.ErrorInformation;
import org.mojave.scheme.fspiop.core.ErrorInformationObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;

public class FspiopServiceErrorWriter implements AuthenticationErrorWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspiopServiceErrorWriter.class);

    private final ObjectMapper objectMapper;

    public FspiopServiceErrorWriter(ObjectMapper objectMapper) {

        assert objectMapper != null;

        this.objectMapper = objectMapper;
    }

    @Override
    public void write(HttpServletResponse response, AuthenticationFailureException exception) {

        PrintWriter writer = null;

        try {

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            writer = response.getWriter();

            if (exception instanceof FspiopServiceGatekeeper.GatekeeperFailureException gke) {

                var cause = (FspiopException) gke.getCause();
                response.setStatus(gke.getStatusCode());
                writer.write(this.objectMapper.writeValueAsString(cause.toErrorObject()));

            } else {

                var error = new ErrorInformationObject().errorInformation(new ErrorInformation(
                    FspiopErrors.GENERIC_SERVER_ERROR.errorType().getCode(),
                    FspiopErrors.GENERIC_SERVER_ERROR.description()));

                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                writer.write(this.objectMapper.writeValueAsString(error));
            }

        } catch (JsonProcessingException e) {

            assert writer != null;

            String errorCode = FspiopErrors.GENERIC_SERVER_ERROR.errorType().getCode();
            String errorDescription = FspiopErrors.GENERIC_SERVER_ERROR.description();

            var json = "{\"errorInformation\":{\"errorCode\": \"" + errorCode +
                           "\",\"errorDescription\":\"" + errorDescription + "\"}}";

            LOGGER.error("Error:", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.write(json);

        } catch (IOException e) {

            LOGGER.error("Error:", e);
            throw new RuntimeException(e);
        }
    }

}
