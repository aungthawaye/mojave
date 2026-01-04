package org.mojave.core.settlement.domain.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
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
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.scheme.common.datatype.converter.identifier.participant.FspIdJavaType;
import org.mojave.scheme.common.datatype.converter.identifier.participant.SspIdJavaType;
import org.mojave.scheme.common.datatype.converter.identifier.settlement.SettlementDefinitionIdJavaType;
import org.mojave.scheme.common.datatype.enums.ActivationStatus;
import org.mojave.scheme.common.datatype.identifier.participant.FspId;
import org.mojave.scheme.common.datatype.identifier.participant.SspId;
import org.mojave.scheme.common.datatype.identifier.settlement.SettlementDefinitionId;
import org.mojave.scheme.fspiop.core.Currency;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "stm_settlement_definition",
    uniqueConstraints = @UniqueConstraint(
        name = "stm_settlement_definition_01_UK",
        columnNames = {
            "payer_fsp_id",
            "payee_fsp_id",
            "currency"}))
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

    @Basic
    @JavaType(FspIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "payer_fsp_id",
        nullable = false)
    protected FspId payerFspId;

    @Basic
    @JavaType(FspIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "payee_fsp_id",
        nullable = false)
    protected FspId payeeFspId;

    @Column(
        name = "currency",
        nullable = false,
        length = StringSizeConstraints.MAX_CURRENCY_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Currency currency;

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

}
