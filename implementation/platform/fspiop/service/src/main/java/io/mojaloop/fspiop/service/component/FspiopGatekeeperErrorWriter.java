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

package io.mojaloop.fspiop.service.component;

import io.mojaloop.component.web.security.spring.AuthenticationErrorWriter;
import io.mojaloop.component.web.security.spring.AuthenticationFailureException;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.component.web.FspiopErrorWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FspiopGatekeeperErrorWriter implements AuthenticationErrorWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspiopGatekeeperErrorWriter.class);

    private final FspiopErrorWriter fspiopErrorWriter;

    public FspiopGatekeeperErrorWriter(FspiopErrorWriter fspiopErrorWriter) {

        assert fspiopErrorWriter != null;

        this.fspiopErrorWriter = fspiopErrorWriter;
    }

    @Override
    public void write(HttpServletResponse response, AuthenticationFailureException exception) {

        if (exception instanceof FspiopServiceGatekeeper.GatekeeperFailureException ge) {

            var cause = (FspiopException) ge.getCause();

            this.fspiopErrorWriter.write(ge.getStatusCode(), response, cause);

        } else {

            this.fspiopErrorWriter.write(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                         response,
                                         new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, exception.getMessage()));
        }
    }

}
