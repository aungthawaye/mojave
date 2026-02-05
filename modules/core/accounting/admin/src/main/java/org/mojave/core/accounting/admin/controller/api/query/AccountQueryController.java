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
package org.mojave.core.accounting.admin.controller.api.query;

import org.mojave.component.misc.query.PagedResult;
import org.mojave.core.accounting.contract.data.AccountData;
import org.mojave.core.accounting.contract.query.AccountQuery;
import org.mojave.common.datatype.identifier.accounting.AccountId;
import org.mojave.common.datatype.identifier.accounting.AccountOwnerId;
import org.mojave.common.datatype.type.accounting.AccountCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/accounting")
public class AccountQueryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        AccountQueryController.class.getName());

    private final AccountQuery accountQuery;

    public AccountQueryController(final AccountQuery accountQuery) {

        Objects.requireNonNull(accountQuery);

        this.accountQuery = accountQuery;
    }

    @PostMapping("/accounts/find-accounts")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PagedResult<AccountData> find(@RequestBody final AccountQuery.Criteria criteria) {

        return this.accountQuery.find(criteria);
    }

    @GetMapping("/accounts/get-all-accounts")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<AccountData> getAll() {

        return this.accountQuery.getAll();
    }

    @GetMapping("/accounts/get-by-account-code")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public AccountData getByAccountCode(@RequestParam final String accountCode) {

        return this.accountQuery.get(new AccountCode(accountCode));
    }

    @GetMapping("/accounts/get-by-account-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public AccountData getByAccountId(@RequestParam final String accountId) {

        return this.accountQuery.get(new AccountId(Long.parseLong(accountId)));
    }

    @GetMapping("/accounts/get-by-owner-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<AccountData> getByOwnerId(@RequestParam final String ownerId) {

        return this.accountQuery.get(new AccountOwnerId(Long.parseLong(ownerId)));
    }

}
