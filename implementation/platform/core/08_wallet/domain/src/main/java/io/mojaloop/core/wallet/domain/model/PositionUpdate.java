package io.mojaloop.core.wallet.domain.model;

import io.mojaloop.component.jpa.JpaInstantConverter;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.converter.identifier.transaction.TransactionIdConverter;
import io.mojaloop.core.common.datatype.converter.identifier.wallet.PositionIdConverter;
import io.mojaloop.core.common.datatype.converter.identifier.wallet.PositionUpdateIdConverter;
import io.mojaloop.core.common.datatype.converter.identifier.wallet.PositionUpdateIdJavaType;
import io.mojaloop.core.common.datatype.enums.wallet.PositionAction;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;
import java.time.Instant;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(name = "wlt_position_update",
    uniqueConstraints = {@UniqueConstraint(name = "wlt_position_update_position_id_action_transaction_id_UK",
        columnNames = {"position_id", "action", "transaction_id"}), @UniqueConstraint(name = "wlt_balance_update_reversed_id_UK",
        columnNames = {"reversed_id"})},
    indexes = {@Index(name = "wlt_position_update_position_id_action_transaction_at_idx",
        columnList = "position_id, action, transaction_at"), @Index(name = "wlt_position_update_transaction_at_idx",
        columnList = "transaction_at")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PositionUpdate {

    @Id
    @JavaType(PositionUpdateIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "position_update_id")
    protected PositionUpdateId id;

    @Column(name = "position_id")
    @Convert(converter = PositionIdConverter.class)
    protected PositionId positionId;

    @Column(name = "action")
    @Enumerated(EnumType.STRING)
    protected PositionAction action;

    @Column(name = "transaction_id")
    @Convert(converter = TransactionIdConverter.class)
    protected TransactionId transactionId;

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    protected Currency currency;

    @Column(name = "amount", precision = 34, scale = 4, nullable = false, updatable = false)
    protected BigDecimal amount;

    @Column(name = "old_position", precision = 34, scale = 4, nullable = false, updatable = false)
    protected BigDecimal oldPosition;

    @Column(name = "new_position", precision = 34, scale = 4, nullable = false, updatable = false)
    protected BigDecimal newPosition;

    @Column(name = "description", length = StringSizeConstraints.MAX_DESCRIPTION_LENGTH)
    protected String description;

    @Column(name = "transaction_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant transactionAt;

    @Column(name = "created_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    @Column(name = "reversed_id")
    @Convert(converter = PositionUpdateIdConverter.class)
    protected PositionUpdateId reversedId;

}
