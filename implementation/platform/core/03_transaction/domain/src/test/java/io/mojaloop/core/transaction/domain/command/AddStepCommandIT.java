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

package io.mojaloop.core.transaction.domain.command;

import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.contract.command.CommitTransactionCommand;
import io.mojaloop.core.transaction.contract.command.OpenTransactionCommand;
import io.mojaloop.core.transaction.contract.exception.TransactionIdNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

class AddStepCommandIT extends BaseDomainIT {

    @Autowired
    private OpenTransactionCommand openTransaction;

    @Autowired
    private AddStepCommand addStep;

    @Autowired
    private CommitTransactionCommand commitTransaction;

    @Test
    void add_step_before_close_phase_before() {

        // Arrange
        var openInput = new OpenTransactionCommand.Input(TransactionType.FUND_TRANSFER_RESERVE);
        var openOutput = this.openTransaction.execute(openInput);

        var params = Map.of("k1", "v1", "k2", "v2");
        var input = new AddStepCommand.Input(openOutput.transactionId(), "reserve", params);

        // Act
        var output = this.addStep.execute(input);

        // Assert
        Assertions.assertNotNull(output);
        Assertions.assertNotNull(output.transactionId());
    }

    @Test
    void add_step_after_close_phase_after() {

        // Arrange
        var openInput = new OpenTransactionCommand.Input(TransactionType.FUND_TRANSFER_COMMIT);
        var openOutput = this.openTransaction.execute(openInput);

        var commitInput = new CommitTransactionCommand.Input(openOutput.transactionId(), null);
        this.commitTransaction.execute(commitInput);

        var params = Map.of("result", "ok");
        var input = new AddStepCommand.Input(openOutput.transactionId(), "commit", params);

        // Act
        var output = this.addStep.execute(input);

        // Assert
        Assertions.assertNotNull(output);
        Assertions.assertNotNull(output.transactionId());
    }

    @Test
    void add_step_throws_when_transaction_not_found() {

        // Arrange
        var fakeId = new TransactionId(1234567890L);
        var input = new AddStepCommand.Input(fakeId, "reserve", Map.of());

        // Act & Assert
        Assertions.assertThrows(TransactionIdNotFoundException.class, () -> this.addStep.execute(input));
    }
}
