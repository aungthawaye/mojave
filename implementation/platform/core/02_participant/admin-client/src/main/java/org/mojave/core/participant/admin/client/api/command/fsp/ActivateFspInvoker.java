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
package org.mojave.core.participant.admin.client.api.command.fsp;

import org.mojave.component.misc.error.RestErrorResponse;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.retrofit.RetrofitService;
import org.mojave.core.participant.admin.client.service.ParticipantAdminService;
import org.mojave.core.participant.contract.command.fsp.ActivateFspCommand;
import org.mojave.core.participant.contract.exception.ParticipantExceptionResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class ActivateFspInvoker implements ActivateFspCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivateFspInvoker.class);

    private final ParticipantAdminService.FspCommand fspCommand;

    private final ObjectMapper objectMapper;

    public ActivateFspInvoker(ParticipantAdminService.FspCommand fspCommand,
                              ObjectMapper objectMapper) {

        assert fspCommand != null;
        assert objectMapper != null;

        this.fspCommand = fspCommand;
        this.objectMapper = objectMapper;
    }

    public ActivateFspCommand.Output execute(ActivateFspCommand.Input input) {

        try {

            return RetrofitService.invoke(
                this.fspCommand.activateFsp(input),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse errorResponse) {

                var throwable = ParticipantExceptionResolver.resolve(errorResponse);

                if (throwable instanceof UncheckedDomainException ude) {
                    throw ude;
                }
            }

            throw new RuntimeException(e);
        }
    }

}
