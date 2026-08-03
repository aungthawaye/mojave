package org.mojave.core.settlement.domain.model;

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
import org.mojave.component.jpa.JpaEntity;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.component.misc.data.DataConversion;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.core.settlement.contract.data.SettlementModelData;
import org.mojave.scheme.rule.converter.identifier.participant.SspIdJavaType;
import org.mojave.scheme.rule.converter.identifier.settlement.SettlementModelIdJavaType;
import org.mojave.scheme.rule.enums.ActivationStatus;
import org.mojave.scheme.rule.enums.TerminationStatus;
import org.mojave.scheme.rule.enums.settlement.SettlementMethod;
import org.mojave.scheme.rule.identifier.participant.SspId;
import org.mojave.scheme.rule.identifier.settlement.SettlementModelId;

import java.util.Objects;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "stl_settlement_model",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "stl_settlement_model_01_UK",
            columnNames = {"name"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SettlementModel extends JpaEntity<SettlementModelId>
    implements DataConversion<SettlementModelData> {

    @Id
    @JavaType(SettlementModelIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "settlement_model_id",
        nullable = false,
        updatable = false)
    protected SettlementModelId id;

    @Column(
        name = "name",
        nullable = false,
        length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String name;

    @Column(
        name = "settlement_method",
        nullable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected SettlementMethod settlementMethod;

    @JavaType(SspIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "ssp_id",
        nullable = false)
    protected SspId sspId;

    @Column(
        name = "activation_status",
        nullable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected ActivationStatus activationStatus = ActivationStatus.ACTIVE;

    @Column(
        name = "termination_status",
        nullable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TerminationStatus terminationStatus = TerminationStatus.ALIVE;

    public SettlementModel(final String name, final SettlementMethod settlementMethod,
                           final SspId sspId) {

        this.id = new SettlementModelId(Snowflake.get().nextId());
        this.name(name);
        this.settlementMethod(settlementMethod);
        this.sspId(sspId);
    }

    public void activate() {

        this.activationStatus = ActivationStatus.ACTIVE;
    }

    @Override
    public SettlementModelData convert() {

        return new SettlementModelData(
            this.id, this.name, this.settlementMethod, this.sspId,
            this.activationStatus, this.terminationStatus);
    }

    public void deactivate() {

        this.activationStatus = ActivationStatus.INACTIVE;
    }

    @Override
    public SettlementModelId getId() {

        return this.id;
    }

    public SettlementModel name(final String name) {

        Objects.requireNonNull(name);

        final var value = name.trim();

        if (value.isBlank()) {
            throw new IllegalArgumentException("Settlement model name is required.");
        }

        if (value.length() > StringSizeConstraints.MAX_NAME_TITLE_LENGTH) {
            throw new IllegalArgumentException("Settlement model name is too long.");
        }

        this.name = value;

        return this;
    }

    public SettlementModel settlementMethod(final SettlementMethod settlementMethod) {

        Objects.requireNonNull(settlementMethod);

        this.settlementMethod = settlementMethod;

        return this;
    }

    public SettlementModel sspId(final SspId sspId) {

        Objects.requireNonNull(sspId);

        this.sspId = sspId;

        return this;
    }

    public void terminate() {

        this.terminationStatus = TerminationStatus.TERMINATED;
    }

}
