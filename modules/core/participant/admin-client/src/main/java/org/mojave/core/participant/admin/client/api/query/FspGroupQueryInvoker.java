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

package org.mojave.core.participant.admin.client.api.query;

import org.mojave.common.datatype.identifier.participant.FspGroupId;
import org.mojave.component.misc.error.RestErrorResponse;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.retrofit.RetrofitService;
import org.mojave.core.participant.admin.client.service.ParticipantAdminService;
import org.mojave.core.participant.contract.data.FspGroupData;
import org.mojave.core.participant.contract.exception.ParticipantExceptionResolver;
import org.mojave.core.participant.contract.query.FspGroupQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

@Component
public class FspGroupQueryInvoker implements FspGroupQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspGroupQueryInvoker.class);

    private final ParticipantAdminService.FspGroupQuery fspGroupQuery;

    private final ObjectMapper objectMapper;

    public FspGroupQueryInvoker(
        final ParticipantAdminService.FspGroupQuery fspGroupQuery, final ObjectMapper objectMapper) {

        Objects.requireNonNull(fspGroupQuery);
        Objects.requireNonNull(objectMapper);

        this.fspGroupQuery = fspGroupQuery;
        this.objectMapper = objectMapper;
    }

    @Override
    public FspGroupData get(final FspGroupId fspGroupId) {

        try {

            return RetrofitService.invoke(
                this.fspGroupQuery.getByFspGroupId(fspGroupId.getId().toString()),
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

    @Override
    public List<FspGroupData> getAll() {

        try {

            return RetrofitService.invoke(
                this.fspGroupQuery.getAllFspGroups(),
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
