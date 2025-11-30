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

package io.mojaloop.core.wallet.intercom.controller.api.command.balance;

import io.mojaloop.core.wallet.contract.command.balance.DepositFundCommand;
import io.mojaloop.core.wallet.contract.exception.balance.NoBalanceUpdateForTransactionException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DepositFundController {

    private final DepositFundCommand depositFundCommand;

    public DepositFundController(final DepositFundCommand depositFundCommand) {

        assert depositFundCommand != null;

        this.depositFundCommand = depositFundCommand;
    }

    @PostMapping("/balances/deposit-fund")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public DepositFundCommand.Output execute(@Valid @RequestBody final DepositFundCommand.Input input)
        throws NoBalanceUpdateForTransactionException {
        return this.depositFundCommand.execute(input);
    }

}
