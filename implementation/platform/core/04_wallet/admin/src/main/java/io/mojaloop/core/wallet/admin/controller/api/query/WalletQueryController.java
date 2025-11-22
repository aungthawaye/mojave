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
package io.mojaloop.core.wallet.admin.controller.api.query;

import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.contract.data.WalletData;
import io.mojaloop.core.wallet.contract.query.WalletQuery;
import io.mojaloop.fspiop.spec.core.Currency;
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
public class WalletQueryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletQueryController.class.getName());

    private final WalletQuery walletQuery;

    public WalletQueryController(final WalletQuery walletQuery) {

        assert walletQuery != null;

        this.walletQuery = walletQuery;
    }

    @GetMapping("/wallets/get-by-wallet-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public WalletData get(@RequestParam final WalletId walletId) {

        return this.walletQuery.get(walletId);
    }

    @GetMapping("/wallets/get-by-owner-id-currency")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<WalletData> get(@RequestParam final WalletOwnerId ownerId, @RequestParam final Currency currency) {

        return this.walletQuery.get(ownerId, currency);
    }

    @GetMapping("/wallets/get-all-wallets")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<WalletData> getAll() {

        return this.walletQuery.getAll();
    }
}
