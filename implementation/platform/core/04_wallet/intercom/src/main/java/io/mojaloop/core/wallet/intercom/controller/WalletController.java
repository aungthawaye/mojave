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

package io.mojaloop.core.wallet.intercom.controller;

import io.mojaloop.core.wallet.contract.command.wallet.DepositFundCommand;
import io.mojaloop.core.wallet.contract.command.wallet.ReverseFundCommand;
import io.mojaloop.core.wallet.contract.command.wallet.WithdrawFundCommand;
import io.mojaloop.core.wallet.contract.data.WalletData;
import io.mojaloop.core.wallet.contract.exception.wallet.InsufficientBalanceInWalletException;
import io.mojaloop.core.wallet.contract.exception.wallet.NoBalanceUpdateForTransactionException;
import io.mojaloop.core.wallet.contract.exception.wallet.ReversalFailedInWalletException;
import io.mojaloop.core.wallet.contract.query.WalletQuery;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WalletController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletController.class);

    private final DepositFundCommand depositFundCommand;

    private final WithdrawFundCommand withdrawFundCommand;

    private final ReverseFundCommand reverseFundCommand;

    private final WalletQuery walletQuery;

    public WalletController(DepositFundCommand depositFundCommand, WithdrawFundCommand withdrawFundCommand, ReverseFundCommand reverseFundCommand, WalletQuery walletQuery) {

        assert depositFundCommand != null;
        assert withdrawFundCommand != null;
        assert reverseFundCommand != null;
        assert walletQuery != null;

        this.depositFundCommand = depositFundCommand;
        this.withdrawFundCommand = withdrawFundCommand;
        this.reverseFundCommand = reverseFundCommand;
        this.walletQuery = walletQuery;
    }

    @GetMapping("/wallets")
    public List<WalletData> execute() {

        return this.walletQuery.getAll();
    }

    @PostMapping("/wallets/deposit-fund")
    public DepositFundCommand.Output execute(@Valid @RequestBody DepositFundCommand.Input input) throws NoBalanceUpdateForTransactionException {

        LOGGER.info("Entering DepositFundCommand.execute: input : {}", input);

        final var output = this.depositFundCommand.execute(input);

        LOGGER.info("Exiting DepositFundCommand.execute: {}", output);

        return output;
    }

    @PostMapping("/wallets/withdraw-fund")
    public WithdrawFundCommand.Output execute(@Valid @RequestBody WithdrawFundCommand.Input input)
        throws NoBalanceUpdateForTransactionException, InsufficientBalanceInWalletException {

        LOGGER.info("Entering WithdrawFundCommand.execute: input : {}", input);

        final var output = this.withdrawFundCommand.execute(input);

        LOGGER.info("Exiting WithdrawFundCommand.execute: {}", output);

        return output;
    }

    @PostMapping("/wallets/reverse-fund")
    public ReverseFundCommand.Output execute(@Valid @RequestBody ReverseFundCommand.Input input) throws ReversalFailedInWalletException {

        LOGGER.info("Entering ReverseFundCommand.execute: input : {}", input);

        final var output = this.reverseFundCommand.execute(input);

        LOGGER.info("Exiting ReverseFundCommand.execute: {}", output);

        return output;
    }

}
