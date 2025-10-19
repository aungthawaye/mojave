package io.mojaloop.core.transaction.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.jpa.JpaInstantConverter;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.converter.identifier.transaction.TransactionIdJavaType;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionPhase;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionStepPhase;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(name = "txn_transaction", indexes = {@Index(name = "txn_transaction_type_stage_initiated_at_IDX", columnList = "type, stage, initiated_at"),
                                            @Index(name = "txn_transaction_type_stage_reserved_at_IDX", columnList = "type, stage, reserved_at"),
                                            @Index(name = "txn_transaction_type_stage_completed_at_IDX", columnList = "type, stage, completed_at"),
                                            @Index(name = "txn_transaction_initiated_at_IDX", columnList = "initiated_at"),
                                            @Index(name = "txn_transaction_reserved_at_IDX", columnList = "reserved_at"),
                                            @Index(name = "txn_transaction_completed_at_IDX", columnList = "completed_at")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction extends JpaEntity<TransactionId> {

    @Id
    @JavaType(TransactionIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "transaction_id", nullable = false, updatable = false)
    protected TransactionId id;

    @Column(name = "type", nullable = false, updatable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TransactionType type;

    @Column(name = "phase", nullable = false, updatable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TransactionPhase stage;

    @Column(name = "status", nullable = false, updatable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TransactionStepPhase status;

    @Column(name = "open_at", nullable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant openAt;

    @Column(name = "commit_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant commitAt;

    @Column(name = "rollback_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant rollbackAt;

    @Column(name = "broken_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant brokenAt;

    @Column(name = "mature")
    protected Boolean mature = false;

    @Column(name = "error", length = StringSizeConstraints.MAX_DESCRIPTION_LENGTH)
    protected String error;

    @OneToMany(mappedBy = "transaction")
    protected List<TransactionStep> steps = new ArrayList<>();

    public Transaction(TransactionId transactionId, TransactionType type) {

        assert transactionId != null;
        assert type != null;

        this.id = transactionId;
        this.type = type;
        this.openAt = Instant.now();
    }

}
