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

package io.mojaloop.core.wallet.domain.command.position;

import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.contract.command.position.CreatePositionCommand;
import io.mojaloop.core.wallet.contract.exception.position.PositionAlreadyExistsException;
import io.mojaloop.core.wallet.domain.command.BaseDomainIT;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreatePositionCommandIT extends BaseDomainIT {

    @Autowired
    private CreatePositionCommand createPositionCommand;

    @Test
    void should_create_position_successfully() {
        // Arrange
        final var input = new CreatePositionCommand.Input(new WalletOwnerId(91001L), Currency.USD, "Main Position", new BigDecimal("100.0000"));

        // Act
        final var output = this.createPositionCommand.execute(input);

        // Assert
        assertNotNull(output);
        assertNotNull(output.positionId());
    }

    @Test
    void should_fail_when_position_already_exists_for_owner_and_currency() {
        // Arrange
        final var ownerId = new WalletOwnerId(91002L);
        final var currency = Currency.USD;
        this.createPositionCommand.execute(new CreatePositionCommand.Input(ownerId, currency, "Position A", new BigDecimal("100.0000")));
        final var input = new CreatePositionCommand.Input(ownerId, currency, "Position B", new BigDecimal("100.0000"));

        // Act & Assert
        assertThrows(PositionAlreadyExistsException.class, () -> this.createPositionCommand.execute(input));
    }

}
