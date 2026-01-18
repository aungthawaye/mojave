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
package org.mojave.core.accounting.intercom.client.api.query;

import org.mojave.component.misc.error.RestErrorResponse;
import org.mojave.component.misc.query.PagedResult;
import org.mojave.component.retrofit.RetrofitService;
import org.mojave.core.accounting.contract.data.AccountData;
import org.mojave.core.accounting.contract.exception.account.AccountCodeNotFoundException;
import org.mojave.core.accounting.contract.exception.account.AccountIdNotFoundException;
import org.mojave.core.accounting.contract.query.AccountQuery;
import org.mojave.core.accounting.intercom.client.service.AccountingIntercomService;
import org.mojave.core.common.datatype.identifier.accounting.AccountId;
import org.mojave.core.common.datatype.identifier.accounting.AccountOwnerId;
import org.mojave.core.common.datatype.type.accounting.AccountCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
public class AccountQueryInvoker implements AccountQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountQueryInvoker.class);

    private final AccountingIntercomService.AccountQuery accountQuery;

    private final ObjectMapper objectMapper;

    public AccountQueryInvoker(final AccountingIntercomService.AccountQuery accountQuery,
                               final ObjectMapper objectMapper) {

        assert accountQuery != null;
        assert objectMapper != null;

        this.accountQuery = accountQuery;
        this.objectMapper = objectMapper;
    }

    @Override
    public PagedResult<AccountData> find(final Criteria criteria) {

        try {

            return RetrofitService.invoke(
                this.accountQuery.find(criteria),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            // No accounting-specific resolver available; wrap as runtime for now.
            throw new RuntimeException(e);
        }
    }

    @Override
    public AccountData get(final AccountCode accountCode) throws AccountCodeNotFoundException {

        try {

            return RetrofitService.invoke(
                this.accountQuery.getByAccountCode(accountCode),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AccountData> get(final AccountOwnerId ownerId) {

        try {

            return RetrofitService.invoke(
                this.accountQuery.getByOwnerId(ownerId),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public AccountData get(final AccountId accountId) throws AccountIdNotFoundException {

        try {

            return RetrofitService.invoke(
                this.accountQuery.getByAccountId(accountId),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AccountData> getAll() {

        try {

            return RetrofitService.invoke(
                this.accountQuery.getAll(),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

}
