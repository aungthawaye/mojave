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
import io.mojaloop.core.common.datatype.identifier.wallet.BalanceUpdateId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.contract.command.wallet.CreateWalletCommand;
import io.mojaloop.core.wallet.contract.command.wallet.DepositFundCommand;
import io.mojaloop.core.wallet.contract.command.wallet.ReverseFundCommand;
import io.mojaloop.core.wallet.contract.command.wallet.WithdrawFundCommand;
import io.mojaloop.core.wallet.domain.command.BaseDomainIT;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class ReverseFundCommandIT extends BaseDomainIT {

    @Autowired
    private CreateWalletCommand createWalletCommand;

    @Autowired
    private DepositFundCommand depositFundCommand;

    @Autowired
    private WithdrawFundCommand withdrawFundCommand;

    @Autowired
    private ReverseFundCommand reverseFundCommand;

    @Test
    void should_fail_to_reverse_deposit() throws io.mojaloop.core.wallet.contract.exception.wallet.NoBalanceUpdateForTransactionException {
        // Arrange
        final var walletOut = this.createWalletCommand.execute(new CreateWalletCommand.Input(new WalletOwnerId(84002L), Currency.USD, "Reverse Fail Wallet"));

        final var depositOut = this.depositFundCommand.execute(
            new DepositFundCommand.Input(walletOut.walletId(), new BigDecimal("15.00"), new TransactionId(8300000000003L), Instant.parse("2025-01-04T10:00:00Z"), "Deposit"));

        final var input = new ReverseFundCommand.Input(depositOut.balanceUpdateId(), new BalanceUpdateId(8300000001000L), "Attempt reverse deposit");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> this.reverseFundCommand.execute(input));
    }

    @Test
    void should_reverse_withdraw_successfully() throws
                                                io.mojaloop.core.wallet.contract.exception.wallet.NoBalanceUpdateForTransactionException,
                                                io.mojaloop.core.wallet.contract.exception.wallet.InsufficientBalanceInWalletException,
                                                io.mojaloop.core.wallet.contract.exception.wallet.ReversalFailedInWalletException {
        // Arrange
        final var walletOut = this.createWalletCommand.execute(new CreateWalletCommand.Input(new WalletOwnerId(84001L), Currency.USD, "Reverse Wallet"));

        // Deposit then withdraw to create a WITHDRAW update to reverse
        this.depositFundCommand.execute(
            new DepositFundCommand.Input(
                walletOut.walletId(), new BigDecimal("100.00"), new TransactionId(8300000000001L), Instant.parse("2025-01-04T08:00:00Z"), "Fund before reverse"));

        final var withdrawOut = this.withdrawFundCommand.execute(
            new WithdrawFundCommand.Input(
                walletOut.walletId(), new BigDecimal("40.00"), new TransactionId(8300000000002L), Instant.parse("2025-01-04T09:00:00Z"), "Withdraw to be reversed"));

        final var reversedId = withdrawOut.balanceUpdateId();
        final var balanceUpdateId = new BalanceUpdateId(8300000000999L);
        final var input = new ReverseFundCommand.Input(reversedId, balanceUpdateId, "Reverse withdraw");

        // Act
        final var output = this.reverseFundCommand.execute(input);

        // Assert
        assertNotNull(output);
        assertEquals(walletOut.walletId(), output.walletId());
        assertEquals(BalanceAction.REVERSE, output.action());
        assertEquals(reversedId, output.reversalId());
        assertNotNull(output.balanceUpdateId());
        // 100 - 40 = 60, reversed adds back 40 → new balance should be 100 again
        assertEquals(0, output.oldBalance().compareTo(new BigDecimal("60.0000")));
        assertEquals(0, output.newBalance().compareTo(new BigDecimal("100.0000")));
        assertNotNull(output.transactionId());
        assertEquals(Currency.USD, output.currency());
    }

}
