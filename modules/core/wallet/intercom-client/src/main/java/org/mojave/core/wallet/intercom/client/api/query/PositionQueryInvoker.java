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
package org.mojave.core.wallet.intercom.client.api.query;

import org.mojave.component.misc.error.RestErrorResponse;
import org.mojave.component.retrofit.RetrofitService;
import org.mojave.common.datatype.identifier.wallet.PositionId;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.contract.data.PositionData;
import org.mojave.core.wallet.contract.query.PositionQuery;
import org.mojave.core.wallet.intercom.client.service.WalletIntercomService;
import org.mojave.common.datatype.enums.Currency;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

@Component
public class PositionQueryInvoker implements PositionQuery {

    private final WalletIntercomService.PositionQuery positionQuery;

    private final ObjectMapper objectMapper;

    public PositionQueryInvoker(final WalletIntercomService.PositionQuery positionQuery,
                                final ObjectMapper objectMapper) {

        Objects.requireNonNull(positionQuery);
        Objects.requireNonNull(objectMapper);

        this.positionQuery = positionQuery;
        this.objectMapper = objectMapper;
    }

    @Override
    public PositionData get(final PositionId positionId) {

        try {

            return RetrofitService.invoke(
                this.positionQuery.getByPositionId(positionId),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PositionData> get(final WalletOwnerId ownerId, final Currency currency) {

        try {

            return RetrofitService.invoke(
                this.positionQuery.getByOwnerIdAndCurrency(ownerId, currency),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PositionData> getAll() {

        try {

            return RetrofitService.invoke(
                this.positionQuery.getAll(),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

}
