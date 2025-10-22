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
package io.mojaloop.core.accounting.admin.controller.account;

import io.mojaloop.core.accounting.contract.data.AccountData;
import io.mojaloop.core.accounting.contract.exception.account.AccountCodeNotFoundException;
import io.mojaloop.core.accounting.contract.exception.account.AccountIdNotFoundException;
import io.mojaloop.core.accounting.contract.query.AccountQuery;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountId;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountOwnerId;
import io.mojaloop.core.common.datatype.type.accounting.AccountCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GetAccountController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetAccountController.class);

    private final AccountQuery accountQuery;

    public GetAccountController(AccountQuery accountQuery) {

        assert accountQuery != null;

        this.accountQuery = accountQuery;
    }

    @GetMapping("/accounts/get-all-accounts")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<AccountData> allAccounts() {

        return this.accountQuery.getAll();
    }

    @GetMapping("/accounts/get-by-account-code")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public AccountData byAccountCode(@RequestParam String accountCode) throws AccountCodeNotFoundException {

        return this.accountQuery.get(new AccountCode(accountCode));
    }

    @GetMapping("/accounts/get-by-account-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public AccountData byAccountId(@RequestParam Long accountId) throws AccountIdNotFoundException {

        return this.accountQuery.get(new AccountId(accountId));
    }

    @GetMapping("/accounts/get-by-owner-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<AccountData> byOwnerId(@RequestParam Long ownerId) {

        return this.accountQuery.get(new AccountOwnerId(ownerId));
    }

}
