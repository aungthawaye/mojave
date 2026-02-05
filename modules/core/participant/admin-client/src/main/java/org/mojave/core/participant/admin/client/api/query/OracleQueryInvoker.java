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

import org.mojave.component.misc.error.RestErrorResponse;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.retrofit.RetrofitService;
import org.mojave.common.datatype.identifier.participant.OracleId;
import org.mojave.core.participant.admin.client.service.ParticipantAdminService;
import org.mojave.core.participant.contract.data.OracleData;
import org.mojave.core.participant.contract.exception.ParticipantExceptionResolver;
import org.mojave.core.participant.contract.exception.oracle.OracleIdNotFoundException;
import org.mojave.core.participant.contract.exception.oracle.OracleTypeNotFoundException;
import org.mojave.core.participant.contract.query.OracleQuery;
import org.mojave.common.datatype.enums.participant.PartyIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.Objects;

@Component
public class OracleQueryInvoker implements OracleQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(OracleQueryInvoker.class);

    private final ParticipantAdminService.OracleQuery oracleQuery;

    private final ObjectMapper objectMapper;

    public OracleQueryInvoker(final ParticipantAdminService.OracleQuery oracleQuery,
                              final ObjectMapper objectMapper) {

        Objects.requireNonNull(oracleQuery);
        Objects.requireNonNull(objectMapper);

        this.oracleQuery = oracleQuery;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<OracleData> find(final PartyIdType type) {

        try {

            var data = RetrofitService.invoke(
                this.oracleQuery.getByPartyIdType(type.name()),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();
            return Optional.ofNullable(data);

        } catch (RetrofitService.InvocationException e) {

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse errorResponse) {

                var throwable = ParticipantExceptionResolver.resolve(errorResponse);

                if (throwable instanceof OracleTypeNotFoundException) {
                    return Optional.empty();
                }

                if (throwable instanceof UncheckedDomainException ude) {
                    throw ude;
                }
            }

            throw new RuntimeException(e);
        }
    }

    @Override
    public OracleData get(final PartyIdType type) {

        try {

            return RetrofitService.invoke(
                this.oracleQuery.getByPartyIdType(type.name()),
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
    public OracleData get(final OracleId oracleId) {

        try {

            return RetrofitService.invoke(
                this.oracleQuery.getByOracleId(oracleId.getId().toString()),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse errorResponse) {

                var throwable = ParticipantExceptionResolver.resolve(errorResponse);

                if (throwable instanceof OracleIdNotFoundException) {
                    // fall through to general path which will rethrow
                }

                if (throwable instanceof UncheckedDomainException ude) {
                    throw ude;
                }
            }

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<OracleData> getAll() {

        try {

            return RetrofitService.invoke(
                this.oracleQuery.getAllOracles(),
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
