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

package org.mojave.participant.admin.client.api.query;

import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.retrofit.RetrofitService;
import org.mojave.participant.admin.client.service.ParticipantAdminService;
import org.mojave.participant.contract.data.HubData;
import org.mojave.participant.contract.exception.ParticipantExceptionResolver;
import org.mojave.participant.contract.query.HubQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

@Component
public class HubQueryInvoker implements HubQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(HubQueryInvoker.class);

    private final ParticipantAdminService.HubQuery hubQuery;

    private final ObjectMapper objectMapper;

    public HubQueryInvoker(final ParticipantAdminService.HubQuery hubQuery,
                           final ObjectMapper objectMapper) {

        Objects.requireNonNull(hubQuery);
        Objects.requireNonNull(objectMapper);

        this.hubQuery = hubQuery;
        this.objectMapper = objectMapper;
    }

    @Override
    public long count() {

        try {

            return RetrofitService.invoke(
                this.hubQuery.count(),
                (status, errorResponseBody) -> MojaveErrorResponse.from(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof MojaveErrorResponse errorResponse) {

                var throwable = ParticipantExceptionResolver.resolve(errorResponse);

                if (throwable instanceof UncheckedDomainException ude) {
                    throw ude;
                }
            }

            throw new RuntimeException(e);
        }
    }

    @Override
    public HubData get() {

        try {

            return RetrofitService.invoke(
                this.hubQuery.get(),
                (status, errorResponseBody) -> MojaveErrorResponse.from(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof MojaveErrorResponse errorResponse) {

                var throwable = ParticipantExceptionResolver.resolve(errorResponse);

                if (throwable instanceof UncheckedDomainException ude) {
                    throw ude;
                }
            }

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<HubData> getAll() {

        try {

            return RetrofitService.invoke(
                this.hubQuery.getAll(),
                (status, errorResponseBody) -> MojaveErrorResponse.from(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof MojaveErrorResponse errorResponse) {

                var throwable = ParticipantExceptionResolver.resolve(errorResponse);

                if (throwable instanceof UncheckedDomainException ude) {
                    throw ude;
                }
            }

            throw new RuntimeException(e);
        }
    }

}
