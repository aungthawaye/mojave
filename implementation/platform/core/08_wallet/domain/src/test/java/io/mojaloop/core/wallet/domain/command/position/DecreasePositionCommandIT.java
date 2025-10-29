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
import io.mojaloop.core.wallet.contract.command.position.DecreasePositionCommand;
import io.mojaloop.core.wallet.contract.command.position.IncreasePositionCommand;
import io.mojaloop.core.wallet.domain.command.BaseDomainIT;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class DecreasePositionCommandIT extends BaseDomainIT {

    @Autowired
    private CreatePositionCommand createPositionCommand;

    @Autowired
    private IncreasePositionCommand increasePositionCommand;

    @Autowired
    private DecreasePositionCommand decreasePositionCommand;

    @Test
    void should_decrease_position_successfully() {
        // Arrange
        final var createOut = this.createPositionCommand.execute(
            new CreatePositionCommand.Input(new WalletOwnerId(86001L), Currency.USD, "Position D",
                new BigDecimal("500.0000")));
        final var positionId = createOut.positionId();

        // Seed initial position to 100.0000 by increasing first
        final var seedTxId = new TransactionId(8600000000001L);
        final var seedTxAt = Instant.parse("2025-01-06T08:30:00Z");
        this.increasePositionCommand.execute(
            new IncreasePositionCommand.Input(positionId, new BigDecimal("100.0000"), seedTxId, seedTxAt,
                "Seed position"));

        final var txId = new TransactionId(8600000001001L);
        final var txAt = Instant.parse("2025-01-06T09:00:00Z");
        final var amount = new BigDecimal("30.00");

        final var input = new DecreasePositionCommand.Input(positionId, amount, txId, txAt, "Decrease for test");

        // Act
        final var output = this.decreasePositionCommand.execute(input);

        // Assert
        assertNotNull(output);
        assertEquals(positionId, output.positionId());
        assertEquals(PositionAction.DECREASE, output.action());
        assertEquals(txId, output.transactionId());
        assertEquals(Currency.USD, output.currency());
        assertEquals(0, output.amount().compareTo(amount));
        assertEquals(0, output.oldPosition().compareTo(new BigDecimal("100.0000")));
        assertEquals(0, output.newPosition().compareTo(new BigDecimal("70.0000")));
        assertEquals(0, output.oldReserved().compareTo(new BigDecimal("0.0000")));
        assertEquals(0, output.newReserved().compareTo(new BigDecimal("0.0000")));
        assertEquals(txAt, output.transactionAt());
        assertNotNull(output.positionUpdateId());
    }
}
