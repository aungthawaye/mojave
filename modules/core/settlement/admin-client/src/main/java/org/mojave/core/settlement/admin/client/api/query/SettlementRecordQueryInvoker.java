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

package org.mojave.core.settlement.admin.client.api.query;

import org.mojave.common.datatype.identifier.settlement.SettlementRecordId;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.component.misc.error.RestErrorResponse;
import org.mojave.component.retrofit.RetrofitService;
import org.mojave.core.settlement.admin.client.service.SettlementAdminService;
import org.mojave.core.settlement.contract.data.SettlementRecordData;
import org.mojave.core.settlement.contract.query.SettlementRecordQuery;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

@Component
public class SettlementRecordQueryInvoker implements SettlementRecordQuery {

    private final SettlementAdminService.RecordQuery recordQuery;

    private final ObjectMapper objectMapper;

    public SettlementRecordQueryInvoker(final SettlementAdminService.RecordQuery recordQuery,
                                        final ObjectMapper objectMapper) {

        Objects.requireNonNull(recordQuery);
        Objects.requireNonNull(objectMapper);

        this.recordQuery = recordQuery;
        this.objectMapper = objectMapper;
    }

    @Override
    public SettlementRecordData get(final SettlementRecordId settlementRecordId) {

        try {

            return RetrofitService.invoke(
                this.recordQuery.getBySettlementRecordId(settlementRecordId),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SettlementRecordData> get(final TransactionId transactionId) {

        try {

            return RetrofitService.invoke(
                this.recordQuery.getByTransactionId(transactionId),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SettlementRecordData> getAll() {

        try {

            return RetrofitService.invoke(
                this.recordQuery.getAllSettlementRecords(),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

}
