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
import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.admin.client.service.WalletAdminService;
import io.mojaloop.core.wallet.contract.data.WalletData;
import io.mojaloop.core.wallet.contract.query.WalletQuery;
import io.mojaloop.fspiop.spec.core.Currency;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WalletQueryInvoker implements WalletQuery {

    private final WalletAdminService.WalletQuery walletQuery;

    private final ObjectMapper objectMapper;

    public WalletQueryInvoker(final WalletAdminService.WalletQuery walletQuery,
                              final ObjectMapper objectMapper) {

        assert walletQuery != null;
        assert objectMapper != null;

        this.walletQuery = walletQuery;
        this.objectMapper = objectMapper;
    }

    @Override
    public WalletData get(final WalletId walletId) {

        try {

            return RetrofitService
                       .invoke(
                           this.walletQuery.getByWalletId(walletId),
                           (status, errorResponseBody) -> RestErrorResponse.decode(
                               errorResponseBody, this.objectMapper))
                       .body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<WalletData> get(final WalletOwnerId ownerId, final Currency currency) {

        try {

            return RetrofitService
                       .invoke(
                           this.walletQuery.getByOwnerIdAndCurrency(ownerId, currency),
                           (status, errorResponseBody) -> RestErrorResponse.decode(
                               errorResponseBody, this.objectMapper))
                       .body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<WalletData> getAll() {

        try {

            return RetrofitService
                       .invoke(
                           this.walletQuery.getAll(),
                           (status, errorResponseBody) -> RestErrorResponse.decode(
                               errorResponseBody, this.objectMapper))
                       .body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

}
