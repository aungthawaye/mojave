package org.mojave.core.settlement.domain.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.mojave.common.datatype.converter.identifier.participant.FspGroupIdJavaType;
import org.mojave.common.datatype.converter.identifier.participant.SspIdJavaType;
import org.mojave.common.datatype.converter.identifier.settlement.SettlementDefinitionIdJavaType;
import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.participant.FspGroupId;
import org.mojave.common.datatype.identifier.participant.SspId;
import org.mojave.common.datatype.identifier.settlement.SettlementDefinitionId;
import org.mojave.component.jpa.JpaEntity;
import org.mojave.component.jpa.JpaInstantConverter;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.component.misc.data.DataConversion;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.core.settlement.contract.data.SettlementDefinitionData;

import java.time.Instant;
import java.util.Objects;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "stm_settlement_definition",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "stm_settlement_definition_01_UK",
            columnNames = {
                "name"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SettlementDefinition extends JpaEntity<SettlementDefinitionId>
    implements DataConversion<SettlementDefinitionData> {

    @Id
    @JavaType(SettlementDefinitionIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "settlement_definition_id")
    protected SettlementDefinitionId id;

    @Column(
        name = "name",
        nullable = false,
        length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String name;

    @Basic
    @JavaType(FspGroupIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "payer_fsp_group_id",
        nullable = false)
    protected FspGroupId payerFspGroupId;

    @Basic
    @JavaType(FspGroupIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "payee_fsp_group_id",
        nullable = false)
    protected FspGroupId payeeFspGroupId;

    @Column(
        name = "currency",
        nullable = false,
        length = StringSizeConstraints.MAX_CURRENCY_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Currency currency;

    @Column(
        name = "start_at",
        nullable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant startAt;

    @Column(name = "end_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant endAt;

    @Basic
    @JavaType(SspIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "desired_provider_id",
        nullable = false)
    protected SspId desiredProviderId;

    @Column(
        name = "activation_status",
        nullable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected ActivationStatus activationStatus;

    public SettlementDefinition(final String name,
                                final FspGroupId payerFspGroupId,
                                final FspGroupId payeeFspGroupId,
                                final Currency currency,
                                final Instant startAt,
                                final Instant endAt,
                                final SspId desiredProviderId) {

        Objects.requireNonNull(name);
        Objects.requireNonNull(payerFspGroupId);
        Objects.requireNonNull(payeeFspGroupId);
        Objects.requireNonNull(currency);
        Objects.requireNonNull(startAt);
        Objects.requireNonNull(desiredProviderId);

        this.validateTimeRange(startAt, endAt);

        this.id = new SettlementDefinitionId(Snowflake.get().nextId());
        this.name = name;
        this.payerFspGroupId = payerFspGroupId;
        this.payeeFspGroupId = payeeFspGroupId;
        this.currency = currency;
        this.startAt = startAt;
        this.endAt = endAt;
        this.desiredProviderId = desiredProviderId;
        this.activationStatus = ActivationStatus.ACTIVE;
    }

    public void activate() {

        this.activationStatus = ActivationStatus.ACTIVE;
    }

    @Override
    public SettlementDefinitionData convert() {

        return new SettlementDefinitionData(
            this.id, this.name, this.payerFspGroupId, this.payeeFspGroupId, this.currency,
            this.startAt, this.endAt, this.desiredProviderId, this.activationStatus);
    }

    public void deactivate() {

        this.activationStatus = ActivationStatus.INACTIVE;
    }

    @Override
    public SettlementDefinitionId getId() {

        return this.id;
    }

    public boolean matches(final Currency currency,
                           final FspGroupId payerFspGroupId,
                           final FspGroupId payeeFspGroupId,
                           final Instant transactionAt) {

        if (!this.currency.equals(currency)) {
            return false;
        }

        if (!this.payerFspGroupId.equals(payerFspGroupId)) {
            return false;
        }

        if (!this.payeeFspGroupId.equals(payeeFspGroupId)) {
            return false;
        }

        if (transactionAt == null) {
            return false;
        }

        if (transactionAt.isBefore(this.startAt)) {
            return false;
        }

        return this.endAt == null || transactionAt.isBefore(this.endAt);
    }

    public boolean overlapsWith(final Instant startAt,
                                final Instant endAt) {

        this.validateTimeRange(startAt, endAt);

        final var overlapStart = this.startAt.isAfter(startAt) ? this.startAt : startAt;

        final Instant overlapEnd;

        if (this.endAt == null && endAt == null) {
            overlapEnd = null;
        } else if (this.endAt == null) {
            overlapEnd = endAt;
        } else if (endAt == null) {
            overlapEnd = this.endAt;
        } else {
            overlapEnd = this.endAt.isBefore(endAt) ? this.endAt : endAt;
        }

        return overlapEnd == null || overlapStart.isBefore(overlapEnd);
    }

    public void update(final String name,
                       final FspGroupId payerFspGroupId,
                       final FspGroupId payeeFspGroupId,
                       final Currency currency,
                       final Instant startAt,
                       final Instant endAt,
                       final SspId desiredProviderId) {

        final var newStartAt = startAt == null ? this.startAt : startAt;
        final var newEndAt = endAt == null ? this.endAt : endAt;

        this.validateTimeRange(newStartAt, newEndAt);

        if (name != null && !name.isBlank()) {
            this.name = name;
        }

        if (payerFspGroupId != null) {
            this.payerFspGroupId = payerFspGroupId;
        }

        if (payeeFspGroupId != null) {
            this.payeeFspGroupId = payeeFspGroupId;
        }

        if (currency != null) {
            this.currency = currency;
        }

        if (startAt != null) {
            this.startAt = startAt;
        }

        if (endAt != null) {
            this.endAt = endAt;
        }

        if (desiredProviderId != null) {
            this.desiredProviderId = desiredProviderId;
        }
    }

    private void validateTimeRange(final Instant startAt,
                                   final Instant endAt) {

        if (startAt == null) {
            throw new IllegalArgumentException("startAt must not be null.");
        }

        if (endAt != null && !startAt.isBefore(endAt)) {
            throw new IllegalArgumentException("startAt must be before endAt.");
        }
    }

}
