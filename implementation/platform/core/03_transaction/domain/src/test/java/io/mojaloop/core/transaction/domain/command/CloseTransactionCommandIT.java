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
import io.mojaloop.core.transaction.contract.command.CloseTransactionCommand;
import io.mojaloop.core.transaction.contract.command.OpenTransactionCommand;
import io.mojaloop.core.transaction.contract.exception.TransactionIdNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CloseTransactionCommandIT extends BaseDomainIT {

    @Autowired
    private OpenTransactionCommand openTransaction;

    @Autowired
    private CloseTransactionCommand commitTransaction;

    @Test
    void commit_successful_transaction() {

        // Arrange
        var openInput = new OpenTransactionCommand.Input(TransactionType.FUND_TRANSFER);
        var openOutput = this.openTransaction.execute(openInput);

        var input = new CloseTransactionCommand.Input(openOutput.transactionId(), null);

        // Act
        var output = this.commitTransaction.execute(input);

        // Assert
        Assertions.assertNotNull(output);
        Assertions.assertNotNull(output.transactionId());
    }

    @Test
    void commit_throws_when_transaction_not_found() {

        // Arrange
        var fakeId = new TransactionId(9876543210L);
        var input = new CloseTransactionCommand.Input(fakeId, null);

        // Act & Assert
        Assertions.assertThrows(TransactionIdNotFoundException.class, () -> this.commitTransaction.execute(input));
    }

    @Test
    void commit_with_error_sets_failure() {

        // Arrange
        var openInput = new OpenTransactionCommand.Input(TransactionType.FUND_TRANSFER);
        var openOutput = this.openTransaction.execute(openInput);

        var input = new CloseTransactionCommand.Input(openOutput.transactionId(), "some error");

        // Act
        var output = this.commitTransaction.execute(input);

        // Assert
        Assertions.assertNotNull(output);
        Assertions.assertNotNull(output.transactionId());
    }

}
