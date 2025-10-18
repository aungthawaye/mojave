package io.mojaloop.core.accounting.domain.cache;

import io.mojaloop.core.accounting.contract.data.FlowDefinitionData;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.accounting.FlowDefinitionId;
import io.mojaloop.fspiop.spec.core.Currency;

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
