package org.mojave.core.settlement.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
import org.mojave.component.jpa.JpaInstantConverter;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.component.misc.data.DataConversion;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.core.settlement.contract.data.SettlementWindowData;
import org.mojave.core.settlement.contract.exception.RequireBatchModelException;
import org.mojave.scheme.rule.converter.identifier.participant.FspGroupIdJavaType;
import org.mojave.scheme.rule.converter.identifier.settlement.SettlementWindowIdJavaType;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.settlement.SettlementMethod;
import org.mojave.scheme.rule.enums.settlement.SettlementWindowStatus;
import org.mojave.scheme.rule.identifier.participant.FspGroupId;
import org.mojave.scheme.rule.identifier.settlement.SettlementWindowId;

import java.time.Instant;
import java.util.Objects;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "stl_settlement_window",
    indexes = {
        @Index(
            name = "stl_settlement_window_01_IDX",
            columnList = "settlement_model_id, currency, fsp_group_id, status"),
        @Index(
            name = "stl_settlement_window_02_IDX",
            columnList = "period_start_at, period_end_at")},
    uniqueConstraints = {
        @UniqueConstraint(
            name = "stl_settlement_window_01_UK",
            columnNames = {
                "settlement_model_id",
                "currency",
                "fsp_group_id",
                "period_start_at"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SettlementWindow extends JpaEntity<SettlementWindowId>
    implements DataConversion<SettlementWindowData> {

    @Id
    @JavaType(SettlementWindowIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "settlement_window_id",
        nullable = false,
        updatable = false)
    protected SettlementWindowId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
        name = "settlement_model_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "stl_settlement_model_stl_settlement_window_FK"))
    protected SettlementModel settlementModel;

    @Column(
        name = "currency",
        nullable = false,
        updatable = false,
        length = StringSizeConstraints.MAX_CURRENCY_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Currency currency;

    @JavaType(FspGroupIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "fsp_group_id",
        nullable = false,
        updatable = false)
    protected FspGroupId fspGroupId;

    @Column(
        name = "period_start_at",
        nullable = false,
        updatable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant periodStartAt;

    @Column(name = "period_end_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant periodEndAt;

    @Column(
        name = "status",
        nullable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected SettlementWindowStatus status = SettlementWindowStatus.OPEN;

    public SettlementWindow(final SettlementModel settlementModel, final Currency currency,
                            final FspGroupId fspGroupId, final Instant periodStartAt) {

        Objects.requireNonNull(settlementModel);

        if (settlementModel.getSettlementMethod() != SettlementMethod.BATCH) {
            throw new RequireBatchModelException();
        }

        this.id = new SettlementWindowId(Snowflake.get().nextId());
        this.settlementModel = settlementModel;
        this.currency = Objects.requireNonNull(currency);
        this.fspGroupId = Objects.requireNonNull(fspGroupId);
        this.periodStartAt = Objects.requireNonNull(periodStartAt);
    }

    public boolean accepts(final SettlementModel settlementModel, final Currency currency) {

        return this.status == SettlementWindowStatus.OPEN &&
                   this.settlementModel.equals(settlementModel) && this.currency == currency;
    }

    public void close(final Instant periodEndAt) {

        Objects.requireNonNull(periodEndAt);

        this.periodEndAt = periodEndAt;
        this.status = SettlementWindowStatus.CLOSED;
    }

    @Override
    public SettlementWindowData convert() {

        return new SettlementWindowData(
            this.id, this.settlementModel.getId(), this.currency, this.fspGroupId,
            this.periodStartAt, this.periodEndAt, this.status);
    }

    public void fail() {

        this.status = SettlementWindowStatus.FAILED;
    }

    @Override
    public SettlementWindowId getId() {

        return this.id;
    }

    public boolean isOpen() {

        return this.status == SettlementWindowStatus.OPEN;
    }

    public void settle() {

        this.status = SettlementWindowStatus.SETTLED;
    }

    public void settling() {

        this.status = SettlementWindowStatus.SETTLING;
    }

}
