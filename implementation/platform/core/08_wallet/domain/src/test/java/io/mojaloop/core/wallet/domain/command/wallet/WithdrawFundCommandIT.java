/*-
 * =============================================================================
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
 * =============================================================================
 */

package io.mojaloop.core.wallet.domain.command.wallet;

import io.mojaloop.core.common.datatype.enums.wallet.BalanceAction;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.contract.command.wallet.CreateWalletCommand;
import io.mojaloop.core.wallet.contract.command.wallet.DepositFundCommand;
import io.mojaloop.core.wallet.contract.command.wallet.WithdrawFundCommand;
import io.mojaloop.core.wallet.domain.command.BaseDomainIT;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class WithdrawFundCommandIT extends BaseDomainIT {

    @Autowired
    private CreateWalletCommand createWalletCommand;

    @Autowired
    private DepositFundCommand depositFundCommand;

    @Autowired
    private WithdrawFundCommand withdrawFundCommand;

    @Test
    void should_fail_with_insufficient_balance_on_empty_wallet() {
        // Arrange
        final var walletOut = this.createWalletCommand.execute(new CreateWalletCommand.Input(new WalletOwnerId(83002L), Currency.USD, "Empty Wallet"));

        final var withdrawTxId = new TransactionId(8200000000003L);
        final var withdrawTxAt = Instant.parse("2025-01-03T10:00:00Z");
        final var withdrawAmount = new BigDecimal("10.00");
        final var input = new WithdrawFundCommand.Input(walletOut.walletId(), withdrawAmount, withdrawTxId, withdrawTxAt, "Withdraw should fail");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> this.withdrawFundCommand.execute(input));
    }

    @Test
    void should_withdraw_fund_successfully_when_sufficient_balance() throws
                                                                     io.mojaloop.core.wallet.contract.exception.wallet.NoBalanceUpdateForTransactionException,
                                                                     io.mojaloop.core.wallet.contract.exception.wallet.InsufficientBalanceInWalletException {
        // Arrange
        final var walletOut = this.createWalletCommand.execute(new CreateWalletCommand.Input(new WalletOwnerId(83001L), Currency.USD, "Test Wallet"));

        final var depositTxId = new TransactionId(8200000000001L);
        final var depositTxAt = Instant.parse("2025-01-03T08:00:00Z");
        final var depositAmount = new BigDecimal("200.00");
        this.depositFundCommand.execute(new DepositFundCommand.Input(walletOut.walletId(), depositAmount, depositTxId, depositTxAt, "Top up"));

        final var withdrawTxId = new TransactionId(8200000000002L);
        final var withdrawTxAt = Instant.parse("2025-01-03T09:00:00Z");
        final var withdrawAmount = new BigDecimal("20.00");
        final var input = new WithdrawFundCommand.Input(walletOut.walletId(), withdrawAmount, withdrawTxId, withdrawTxAt, "Withdraw for test");

        // Act
        final var output = this.withdrawFundCommand.execute(input);

        // Assert
        assertNotNull(output);
        assertEquals(walletOut.walletId(), output.walletId());
        assertEquals(BalanceAction.WITHDRAW, output.action());
        assertEquals(withdrawTxId, output.transactionId());
        assertEquals(Currency.USD, output.currency());
        assertEquals(0, output.amount().compareTo(withdrawAmount));
        assertEquals(0, output.oldBalance().compareTo(new BigDecimal("200.0000")));
        assertEquals(0, output.newBalance().compareTo(new BigDecimal("180.0000")));
        assertNotNull(output.transactionAt());
        assertNotNull(output.balanceUpdateId());
    }

}
