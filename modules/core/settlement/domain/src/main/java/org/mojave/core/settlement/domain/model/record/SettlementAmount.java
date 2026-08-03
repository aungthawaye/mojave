package org.mojave.core.settlement.domain.model.record;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.mojave.component.jpa.JpaEntity;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.scheme.rule.converter.identifier.participant.FspIdJavaType;
import org.mojave.scheme.rule.converter.identifier.settlement.SettlementAmountIdJavaType;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.settlement.LiquidityDirection;
import org.mojave.scheme.rule.identifier.participant.FspId;
import org.mojave.scheme.rule.identifier.settlement.SettlementAmountId;

import java.math.BigDecimal;
import java.util.Objects;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "stl_settlement_amount",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "stl_settlement_amount_01_UK",
            columnNames = {
                "settlement_record_id",
                "participant",
                "fsp_id",
                "amount_name",
                "currency",
                "direction"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SettlementAmount extends JpaEntity<SettlementAmountId> {

    @Id
    @JavaType(SettlementAmountIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "settlement_amount_id",
        nullable = false,
        updatable = false)
    protected SettlementAmountId id;

    @Column(
        name = "participant",
        nullable = false,
        length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String participant;

    @JavaType(FspIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "fsp_id")
    protected FspId fspId;

    @Column(
        name = "amount_name",
        nullable = false,
        length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String amountName;

    @Column(
        name = "amount",
        nullable = false,
        precision = 34,
        scale = 4)
    protected BigDecimal amount;

    @Column(
        name = "currency",
        nullable = false,
        length = StringSizeConstraints.MAX_CURRENCY_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Currency currency;

    @Column(
        name = "direction",
        nullable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected LiquidityDirection direction;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
        name = "settlement_record_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "stl_settlement_record_stl_settlement_amount_FK"))
    protected SettlementRecord settlementRecord;

    public SettlementAmount(final SettlementRecord settlementRecord, final String participant,
                            final FspId fspId, final String amountName, final BigDecimal amount,
                            final Currency currency, final LiquidityDirection direction) {

        Objects.requireNonNull(settlementRecord);

        this.id = new SettlementAmountId(Snowflake.get().nextId());
        this.settlementRecord = settlementRecord;

        this.forSettlementAmount(participant, fspId, amountName, amount, currency, direction);
    }

    public SettlementAmount forSettlementAmount(final String participant, final FspId fspId,
                                                final String amountName, final BigDecimal amount,
                                                final Currency currency,
                                                final LiquidityDirection direction) {

        Objects.requireNonNull(participant);
        Objects.requireNonNull(amountName);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(currency);
        Objects.requireNonNull(direction);

        final var participantValue = participant.trim();
        final var amountNameValue = amountName.trim().toUpperCase();

        if (participantValue.isBlank()) {
            throw new IllegalArgumentException("Settlement participant is required.");
        }

        if (amountNameValue.isBlank()) {
            throw new IllegalArgumentException("Settlement amount name is required.");
        }

        this.participant = participantValue;
        this.fspId = fspId;
        this.amountName = amountNameValue;
        this.amount = amount;
        this.currency = currency;
        this.direction = direction;

        return this;
    }

    @Override
    public SettlementAmountId getId() {

        return this.id;
    }

}
