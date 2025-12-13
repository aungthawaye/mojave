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
package org.mojave.core.accounting.domain.cache;

import org.mojave.core.accounting.contract.data.FlowDefinitionData;
import org.mojave.core.common.datatype.enums.trasaction.TransactionType;
import org.mojave.core.common.datatype.identifier.accounting.FlowDefinitionId;
import org.mojave.fspiop.spec.core.Currency;

public interface FlowDefinitionCache {

    void clear();

    void delete(FlowDefinitionId flowDefinitionId);

    FlowDefinitionData get(FlowDefinitionId flowDefinitionId);

    FlowDefinitionData get(TransactionType transactionType, Currency currency);

    void save(FlowDefinitionData flowDefinition);

    class Keys {

        public static String forTransaction(TransactionType transactionType, Currency currency) {

            return transactionType.name() + "_" + currency.name();
        }

    }

    class Qualifiers {

        public static final String REDIS = "redis";

        public static final String IN_MEMORY = "in-memory";

        public static final String DEFAULT = REDIS;

    }

    class Names {

        public static final String WITH_ID = "acc-flow-def-with-id";

        public static final String WITH_TXN_TYPE_CURRENCY = "acc-flow-def-with-txn-type-currency";

    }

}
