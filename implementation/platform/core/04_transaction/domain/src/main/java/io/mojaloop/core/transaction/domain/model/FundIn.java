package io.mojaloop.core.transaction.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;

public class FundIn extends JpaEntity<TransactionId> {

    protected TransactionId id;

    @Override
    public TransactionId getId() {

        return this.id;
    }

}
