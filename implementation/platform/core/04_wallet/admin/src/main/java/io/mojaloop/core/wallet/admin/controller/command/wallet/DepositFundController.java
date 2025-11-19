package io.mojaloop.core.wallet.admin.controller.command.wallet;

/*-
 * ==============================================================================
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
 * ==============================================================================
 */

import io.mojaloop.core.wallet.contract.command.wallet.DepositFundCommand;
import io.mojaloop.core.wallet.contract.exception.wallet.NoBalanceUpdateForTransactionException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DepositFundController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepositFundController.class);

    private final DepositFundCommand depositFundCommand;

    public DepositFundController(final DepositFundCommand depositFundCommand) {

        assert depositFundCommand != null;

        this.depositFundCommand = depositFundCommand;
    }

    @PostMapping("/wallets/deposit-fund")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public DepositFundCommand.Output execute(@Valid @RequestBody final DepositFundCommand.Input input) throws NoBalanceUpdateForTransactionException {

        LOGGER.info("Entering DepositFundCommand.execute: input : {}", input);

        final var output = this.depositFundCommand.execute(input);

        LOGGER.info("Exiting DepositFundCommand.execute: {}", output);

        return output;
    }

}
