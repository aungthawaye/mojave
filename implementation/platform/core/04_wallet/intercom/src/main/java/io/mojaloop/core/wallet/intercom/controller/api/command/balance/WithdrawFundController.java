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

import io.mojaloop.core.wallet.contract.command.balance.WithdrawFundCommand;
import io.mojaloop.core.wallet.contract.exception.balance.InsufficientBalanceException;
import io.mojaloop.core.wallet.contract.exception.balance.NoBalanceUpdateForTransactionException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WithdrawFundController {

    private final WithdrawFundCommand withdrawFundCommand;

    public WithdrawFundController(final WithdrawFundCommand withdrawFundCommand) {

        assert withdrawFundCommand != null;

        this.withdrawFundCommand = withdrawFundCommand;
    }

    @PostMapping("/balances/withdraw-fund")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public WithdrawFundCommand.Output execute(
        @Valid @RequestBody final WithdrawFundCommand.Input input)
        throws NoBalanceUpdateForTransactionException, InsufficientBalanceException {

        return this.withdrawFundCommand.execute(input);
    }

}
