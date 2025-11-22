/*-
 * ================================================================================
 * Mojave
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

package io.mojaloop.core.wallet.admin.client.api.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.admin.client.service.WalletAdminService;
import io.mojaloop.core.wallet.contract.data.PositionData;
import io.mojaloop.core.wallet.contract.query.PositionQuery;
import io.mojaloop.fspiop.spec.core.Currency;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PositionQueryInvoker implements PositionQuery {

    private final WalletAdminService.PositionQuery positionQuery;

    private final ObjectMapper objectMapper;

    public PositionQueryInvoker(final WalletAdminService.PositionQuery positionQuery,
                                final ObjectMapper objectMapper) {

        assert positionQuery != null;
        assert objectMapper != null;

        this.positionQuery = positionQuery;
        this.objectMapper = objectMapper;
    }

    @Override
    public PositionData get(final PositionId positionId) {

        try {

            return RetrofitService
                       .invoke(
                           this.positionQuery.getByPositionId(positionId),
                           (status, errorResponseBody) -> RestErrorResponse.decode(
                               errorResponseBody, this.objectMapper))
                       .body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PositionData> get(final WalletOwnerId ownerId, final Currency currency) {

        try {

            return RetrofitService
                       .invoke(
                           this.positionQuery.getByOwnerIdAndCurrency(ownerId, currency),
                           (status, errorResponseBody) -> RestErrorResponse.decode(
                               errorResponseBody, this.objectMapper))
                       .body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PositionData> getAll() {

        try {

            return RetrofitService
                       .invoke(
                           this.positionQuery.getAll(),
                           (status, errorResponseBody) -> RestErrorResponse.decode(
                               errorResponseBody, this.objectMapper))
                       .body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

}
