/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */
package org.mojave.core.accounting.domain.component.ledger.strategy;

import org.mojave.core.accounting.domain.component.ledger.Ledger;
import org.mojave.core.common.datatype.enums.trasaction.TransactionType;
import org.mojave.core.common.datatype.identifier.transaction.TransactionId;

import java.time.Instant;
import java.util.List;

public class EmptyLedger implements Ledger {

    @Override
    public List<Movement> post(List<Request> requests,
                               TransactionId transactionId,
                               Instant transactionAt,
                               TransactionType transactionType) {

        throw new UnsupportedOperationException("Using EmptyLedger. Posting is not supported.");
    }

}
