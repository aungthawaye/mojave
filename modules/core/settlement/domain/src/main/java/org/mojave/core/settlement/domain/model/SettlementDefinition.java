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
import org.mojave.component.jpa.JpaInstantConverter;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.common.datatype.converter.identifier.participant.SspIdJavaType;
import org.mojave.common.datatype.converter.identifier.settlement.SettlementDefinitionIdJavaType;
import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.identifier.participant.SspId;
import org.mojave.common.datatype.identifier.settlement.SettlementDefinitionId;

import java.time.Instant;

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
public class SettlementDefinition {

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
        mappedBy = "definition",
        fetch = FetchType.EAGER)
    @JoinColumn(
        name = "payer_filter_group_id",
        nullable = false)
    protected FilterGroup payerFilterGroup;

    @OneToOne(
        mappedBy = "definition",
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

    public void activate() {

        this.activationStatus = ActivationStatus.ACTIVE;
    }

    public void deactivate() {

        this.activationStatus = ActivationStatus.INACTIVE;
    }

    public boolean matches(Currency currency, FspId payerFspId, FspId payeeFspId) {

        return this.currency.equals(currency) && this.payerFilterGroup.fspExists(payerFspId) &&
                   this.payeeFilterGroup.fspExists(payeeFspId);
    }

}
