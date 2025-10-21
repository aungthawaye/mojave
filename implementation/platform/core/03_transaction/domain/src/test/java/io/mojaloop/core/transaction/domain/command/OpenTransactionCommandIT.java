/*-
 * ==============================================================================
 * Mojaloop OSS
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
import io.mojaloop.core.transaction.contract.command.OpenTransactionCommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OpenTransactionCommandIT extends BaseDomainIT {

    @Autowired
    private OpenTransactionCommand openTransaction;

    @Test
    void open_transaction_returns_transaction_id() {

        // Arrange
        var input = new OpenTransactionCommand.Input(TransactionType.FUND_IN);

        // Act
        var output = this.openTransaction.execute(input);

        // Assert
        Assertions.assertNotNull(output);
        Assertions.assertNotNull(output.transactionId());
    }
}
