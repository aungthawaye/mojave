package org.mojave.core.settlement.domain.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.mojave.common.datatype.converter.identifier.participant.SspIdJavaType;
import org.mojave.common.datatype.converter.identifier.settlement.SettlementDefinitionIdJavaType;
import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.participant.FspId;
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
                "payer_filter_group_id",
                "payee_filter_group_id",
                "currency"}),
        @UniqueConstraint(
            name = "stm_settlement_definition_02_UK",
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

    @OneToOne(
        fetch = FetchType.EAGER)
    @JoinColumn(
        name = "payer_filter_group_id",
        nullable = false)
    protected FilterGroup payerFilterGroup;

    @OneToOne(
        fetch = FetchType.EAGER)
    @JoinColumn(
        name = "payee_filter_group_id",
        nullable = false)
    protected FilterGroup payeeFilterGroup;

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
                                final FilterGroup payerFilterGroup,
                                final FilterGroup payeeFilterGroup,
                                final Currency currency,
                                final Instant startAt,
                                final SspId desiredProviderId) {

        Objects.requireNonNull(name);
        Objects.requireNonNull(payerFilterGroup);
        Objects.requireNonNull(payeeFilterGroup);
        Objects.requireNonNull(currency);
        Objects.requireNonNull(startAt);
        Objects.requireNonNull(desiredProviderId);

        this.id = new SettlementDefinitionId(Snowflake.get().nextId());
        this.name = name;
        this.payerFilterGroup = payerFilterGroup;
        this.payeeFilterGroup = payeeFilterGroup;
        this.currency = currency;
        this.startAt = startAt;
        this.desiredProviderId = desiredProviderId;
        this.activationStatus = ActivationStatus.ACTIVE;
    }

    public void activate() {

        this.activationStatus = ActivationStatus.ACTIVE;
    }

    @Override
    public SettlementDefinitionData convert() {

        return new SettlementDefinitionData(
            this.id, this.name, this.payerFilterGroup.getId(), this.payeeFilterGroup.getId(),
            this.currency, this.startAt, this.desiredProviderId, this.activationStatus);
    }

    public void deactivate() {

        this.activationStatus = ActivationStatus.INACTIVE;
    }

    @Override
    public SettlementDefinitionId getId() {

        return id;
    }

    public boolean matches(Currency currency, FspId payerFspId, FspId payeeFspId) {

        return this.currency.equals(currency) && this.payerFilterGroup.fspExists(payerFspId) &&
                   this.payeeFilterGroup.fspExists(payeeFspId);
    }

    public void update(final String name,
                       final FilterGroup payerFilterGroup,
                       final FilterGroup payeeFilterGroup,
                       final Currency currency,
                       final Instant startAt,
                       final SspId desiredProviderId) {

        if (name != null && !name.isBlank()) {
            this.name = name;
        }

        if (payerFilterGroup != null) {
            this.payerFilterGroup = payerFilterGroup;
        }

        if (payeeFilterGroup != null) {
            this.payeeFilterGroup = payeeFilterGroup;
        }

        if (currency != null) {
            this.currency = currency;
        }

        if (startAt != null) {
            this.startAt = startAt;
        }

        if (desiredProviderId != null) {
            this.desiredProviderId = desiredProviderId;
        }
    }

}
