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
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.contract.command.wallet.CreateWalletCommand;
import io.mojaloop.core.wallet.contract.command.wallet.DepositFundCommand;
import io.mojaloop.core.wallet.domain.command.BaseDomainIT;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DepositFundCommandIT extends BaseDomainIT {

    @Autowired
    private CreateWalletCommand createWalletCommand;

    @Autowired
    private DepositFundCommand depositFundCommand;

    @Test
    void should_deposit_fund_successfully() throws io.mojaloop.core.wallet.contract.exception.wallet.NoBalanceUpdateForTransactionException {
        // Arrange
        final var walletOut = this.createWalletCommand.execute(new CreateWalletCommand.Input(new WalletOwnerId(82001L), Currency.USD, "Test Wallet"));

        final var txId = new TransactionId(8100000000001L);
        final var txAt = Instant.parse("2025-01-02T09:00:00Z");
        final var amount = new BigDecimal("123.45");

        final var input = new DepositFundCommand.Input(walletOut.walletId(), amount, txId, txAt, "Initial deposit");

        // Act
        final var output = this.depositFundCommand.execute(input);

        // Assert
        assertNotNull(output);
        assertEquals(walletOut.walletId(), output.walletId());
        assertEquals(BalanceAction.DEPOSIT, output.action());
        assertEquals(txId, output.transactionId());
        assertEquals(Currency.USD, output.currency());
        assertEquals(0, output.amount().compareTo(amount));
        assertEquals(0, output.oldBalance().compareTo(new BigDecimal("0.0000")));
        assertEquals(0, output.newBalance().compareTo(new BigDecimal("123.4500")));
        assertEquals(txAt, output.transactionAt());
        assertNotNull(output.balanceUpdateId());
    }

}
