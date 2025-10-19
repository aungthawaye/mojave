package io.mojaloop.core.transaction.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.core.common.datatype.converter.identifier.transaction.TransactionStepIdJavaType;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionStepId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(name = "txn_transaction_step")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionStep extends JpaEntity<TransactionStepId> {

    @Id
    @JavaType(TransactionStepIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    protected TransactionStepId id;

    @Override
    public TransactionStepId getId() {
        return id;
    }

}
