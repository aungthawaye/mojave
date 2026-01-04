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
package org.mojave.core.wallet.admin.client.api.query;

import org.mojave.component.misc.error.RestErrorResponse;
import org.mojave.component.retrofit.RetrofitService;
import org.mojave.scheme.common.datatype.identifier.wallet.BalanceId;
import org.mojave.scheme.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.admin.client.service.WalletAdminService;
import org.mojave.core.wallet.contract.data.BalanceData;
import org.mojave.core.wallet.contract.query.BalanceQuery;
import org.mojave.scheme.fspiop.core.Currency;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
public class BalanceQueryInvoker implements BalanceQuery {

    private final WalletAdminService.BalanceQuery balanceQuery;

    private final ObjectMapper objectMapper;

    public BalanceQueryInvoker(final WalletAdminService.BalanceQuery balanceQuery,
                               final ObjectMapper objectMapper) {

        assert balanceQuery != null;
        assert objectMapper != null;

        this.balanceQuery = balanceQuery;
        this.objectMapper = objectMapper;
    }

    @Override
    public BalanceData get(final BalanceId balanceId) {

        try {

            return RetrofitService.invoke(
                this.balanceQuery.getByWalletId(balanceId),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<BalanceData> get(final WalletOwnerId ownerId, final Currency currency) {

        try {

            return RetrofitService.invoke(
                this.balanceQuery.getByOwnerIdAndCurrency(ownerId, currency),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<BalanceData> getAll() {

        try {

            return RetrofitService.invoke(
                this.balanceQuery.getAll(),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

}
