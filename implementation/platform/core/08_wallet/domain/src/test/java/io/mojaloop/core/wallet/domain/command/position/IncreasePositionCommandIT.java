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

package io.mojaloop.core.wallet.domain.command.position;

import io.mojaloop.core.common.datatype.enums.wallet.PositionAction;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.contract.command.position.CreatePositionCommand;
import io.mojaloop.core.wallet.contract.command.position.IncreasePositionCommand;
import io.mojaloop.core.wallet.contract.exception.position.PositionLimitExceededException;
import io.mojaloop.core.wallet.domain.command.BaseDomainIT;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class IncreasePositionCommandIT extends BaseDomainIT {

    @Autowired
    private CreatePositionCommand createPositionCommand;

    @Autowired
    private IncreasePositionCommand increasePositionCommand;

    @Test
    void should_fail_when_increase_exceeds_net_debit_cap() {
        // Arrange
        final var createOut = this.createPositionCommand.execute(new CreatePositionCommand.Input(new WalletOwnerId(85002L),
                                                                                                 Currency.USD,
                                                                                                 "Cap Position",
                                                                                                 new BigDecimal("50.0000")));
        final var positionId = createOut.positionId();

        final var txId = new TransactionId(8500000001002L);
        final var txAt = Instant.parse("2025-01-05T09:10:00Z");
        final var amount = new BigDecimal("60.00");

        final var input = new IncreasePositionCommand.Input(positionId, amount, txId, txAt, "Should exceed cap");

        // Act & Assert
        assertThrows(PositionLimitExceededException.class, () -> this.increasePositionCommand.execute(input));
    }

    @Test
    void should_increase_position_successfully() throws
                                                 io.mojaloop.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException,
                                                 io.mojaloop.core.wallet.contract.exception.position.PositionLimitExceededException {
        // Arrange
        final var createOut = this.createPositionCommand.execute(new CreatePositionCommand.Input(new WalletOwnerId(85001L),
                                                                                                 Currency.USD,
                                                                                                 "Position I",
                                                                                                 new BigDecimal("100.0000")));
        final var positionId = createOut.positionId();

        final var txId = new TransactionId(8500000001001L);
        final var txAt = Instant.parse("2025-01-05T09:00:00Z");
        final var amount = new BigDecimal("10.00");
        final var input = new IncreasePositionCommand.Input(positionId, amount, txId, txAt, "Increase for test");

        // Act
        final var output = this.increasePositionCommand.execute(input);

        // Assert
        assertNotNull(output);
        assertEquals(positionId, output.positionId());
        assertEquals(PositionAction.INCREASE, output.action());
        assertEquals(txId, output.transactionId());
        assertEquals(Currency.USD, output.currency());
        assertEquals(0, output.amount().compareTo(amount));
        assertEquals(0, output.oldPosition().compareTo(new BigDecimal("0.0000")));
        assertEquals(0, output.newPosition().compareTo(new BigDecimal("10.0000")));
        assertEquals(0, output.oldReserved().compareTo(new BigDecimal("0.0000")));
        assertEquals(0, output.newReserved().compareTo(new BigDecimal("0.0000")));
        assertEquals(txAt, output.transactionAt());
        assertNotNull(output.positionUpdateId());
    }

}
