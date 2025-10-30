package io.mojaloop.core.accounting.domain.component.ledger.strategy;

import io.mojaloop.core.accounting.domain.component.ledger.Ledger;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;

import java.time.Instant;
import java.util.List;

public class EmptyLedger implements Ledger {

    @Override
    public List<Movement> post(List<Request> requests, TransactionId transactionId, Instant transactionAt, TransactionType transactionType) {

        throw new UnsupportedOperationException("Ledger is empty");
    }

}
