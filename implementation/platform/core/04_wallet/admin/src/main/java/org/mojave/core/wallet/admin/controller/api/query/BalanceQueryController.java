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
package org.mojave.core.wallet.admin.controller.api.query;

import org.mojave.core.common.datatype.identifier.wallet.BalanceId;
import org.mojave.core.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.contract.data.BalanceData;
import org.mojave.core.wallet.contract.query.BalanceQuery;
import org.mojave.fspiop.spec.core.Currency;
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
public class BalanceQueryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        BalanceQueryController.class.getName());

    private final BalanceQuery balanceQuery;

    public BalanceQueryController(final BalanceQuery balanceQuery) {

        assert balanceQuery != null;

        this.balanceQuery = balanceQuery;
    }

    @GetMapping("/balances/get-by-balance-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public BalanceData get(@RequestParam final BalanceId balanceId) {

        return this.balanceQuery.get(balanceId);
    }

    @GetMapping("/balances/get-by-owner-id-currency")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<BalanceData> get(@RequestParam final WalletOwnerId ownerId,
                                 @RequestParam final Currency currency) {

        return this.balanceQuery.get(ownerId, currency);
    }

    @GetMapping("/balances/get-all-balances")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<BalanceData> getAll() {

        return this.balanceQuery.getAll();
    }

}
