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
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.contract.command.position.CreatePositionCommand;
import io.mojaloop.core.wallet.contract.command.position.ReservePositionCommand;
import io.mojaloop.core.wallet.contract.command.position.RollbackPositionCommand;
import io.mojaloop.core.wallet.domain.command.BaseDomainIT;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class RollbackPositionCommandIT extends BaseDomainIT {

    @Autowired
    private CreatePositionCommand createPositionCommand;

    @Autowired
    private ReservePositionCommand reservePositionCommand;

    @Autowired
    private RollbackPositionCommand rollbackPositionCommand;

    @Test
    void should_rollback_reservation_successfully() {
        // Arrange
        final var createOut = this.createPositionCommand.execute(
            new CreatePositionCommand.Input(new WalletOwnerId(89001L), Currency.USD, "Position RB",
                new BigDecimal("100.0000")));
        final var positionId = createOut.positionId();

        final var reserveTxId = new TransactionId(8900000001001L);
        final var reserveTxAt = Instant.parse("2025-01-09T09:00:00Z");
        final var amount = new BigDecimal("25.00");

        final var reserveOut = this.reservePositionCommand.execute(
            new ReservePositionCommand.Input(positionId, amount, reserveTxId, reserveTxAt, "Reserve before rollback"));

        final var reservationId = reserveOut.positionUpdateId();
        final var rollbackUpdateId = new PositionUpdateId(8900000099001L);
        final var input = new RollbackPositionCommand.Input(reservationId, rollbackUpdateId, "Rollback reservation");

        // Act
        final var output = this.rollbackPositionCommand.execute(input);

        // Assert
        assertNotNull(output);
        assertEquals(positionId, output.positionId());
        assertEquals(PositionAction.ROLLBACK, output.action());
        assertEquals(Currency.USD, output.currency());
        assertEquals(0, output.amount().compareTo(amount));
        assertEquals(0, output.oldPosition().compareTo(new BigDecimal("0.0000")));
        assertEquals(0, output.newPosition().compareTo(new BigDecimal("0.0000")));
        assertEquals(0, output.oldReserved().compareTo(new BigDecimal("25.0000")));
        assertEquals(0, output.newReserved().compareTo(new BigDecimal("0.0000")));
        assertNotNull(output.transactionAt());
        assertNotNull(output.positionUpdateId());
    }

    @Test
    void should_fail_rollback_when_not_a_reservation() {
        // Arrange
        final var reservationId = new PositionUpdateId(8900000099009L); // non-existent
        final var rollbackUpdateId = new PositionUpdateId(8900000099010L);

        final var input = new RollbackPositionCommand.Input(reservationId, rollbackUpdateId, "Rollback invalid");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> this.rollbackPositionCommand.execute(input));
    }
}
