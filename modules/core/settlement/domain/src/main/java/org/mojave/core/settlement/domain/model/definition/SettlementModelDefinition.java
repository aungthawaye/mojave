package org.mojave.core.settlement.domain.model.definition;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
import org.mojave.core.settlement.contract.data.SettlementModelDefinitionData;
import org.mojave.core.settlement.domain.model.SettlementModel;
import org.mojave.scheme.rule.converter.identifier.participant.FspGroupIdJavaType;
import org.mojave.scheme.rule.converter.identifier.settlement.SettlementDefinitionIdJavaType;
import org.mojave.scheme.rule.enums.ActivationStatus;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.TerminationStatus;
import org.mojave.scheme.rule.enums.settlement.LiquidityDirection;
import org.mojave.scheme.rule.enums.settlement.TransactionPeriod;
import org.mojave.scheme.rule.identifier.participant.FspGroupId;
import org.mojave.scheme.rule.identifier.settlement.SettlementDefinitionId;
import org.mojave.scheme.rule.scenario.ScenarioType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "stl_settlement_model_definition",
    indexes = {
        @Index(
            name = "stl_settlement_model_definition_01_IDX",
            columnList = "scenario, currency, fsp_group_id, transaction_period")},
    uniqueConstraints = {
        @UniqueConstraint(
            name = "stl_settlement_model_definition_01_UK",
            columnNames = {
                "scenario",
                "currency",
                "fsp_group_id",
                "transaction_period"}),
        @UniqueConstraint(
            name = "stl_settlement_model_definition_02_UK",
            columnNames = {"name"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SettlementModelDefinition extends JpaEntity<SettlementDefinitionId>
    implements DataConversion<SettlementModelDefinitionData> {

    @Id
    @JavaType(SettlementDefinitionIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "settlement_model_definition_id",
        nullable = false,
        updatable = false)
    protected SettlementDefinitionId id;

    @Column(
        name = "scenario",
        nullable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected ScenarioType scenario;

    @Column(
        name = "currency",
        nullable = false,
        length = StringSizeConstraints.MAX_CURRENCY_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Currency currency;

    @JavaType(FspGroupIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "fsp_group_id",
        nullable = false)
    protected FspGroupId fspGroupId;

    @Column(
        name = "transaction_period",
        nullable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TransactionPeriod transactionPeriod;

    @Column(
        name = "name",
        nullable = false,
        length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String name;

    @Column(
        name = "description",
        length = StringSizeConstraints.MAX_DESCRIPTION_LENGTH)
    protected String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
        name = "settlement_model_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "stl_settlement_model_stl_settlement_model_definition_FK"))
    protected SettlementModel settlementModel;

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

    @OneToMany(
        mappedBy = "definition",
        orphanRemoval = true,
        cascade = {CascadeType.ALL},
        fetch = FetchType.EAGER)
    protected List<SettlementModelDefinitionLine> settlementModelDefinitionLines = new ArrayList<>();

    public SettlementModelDefinition(final ScenarioType scenario, final Currency currency,
                                     final FspGroupId fspGroupId,
                                     final TransactionPeriod transactionPeriod, final String name,
                                     final String description,
                                     final SettlementModel settlementModel) {

        this.id = new SettlementDefinitionId(Snowflake.get().nextId());
        this.scenario(scenario);
        this.currency(currency);
        this.fspGroupId(fspGroupId);
        this.transactionPeriod(transactionPeriod);
        this.name(name);
        this.description(description);
        this.settlementModel(settlementModel);
    }

    public void activate() {

        this.activationStatus = ActivationStatus.ACTIVE;
    }

    public SettlementModelDefinitionLine addSettlementModelDefinitionLine(final Integer step,
                                                                          final String participant,
                                                                          final String amountName,
                                                                          final Currency currency,
                                                                          final LiquidityDirection direction,
                                                                          final String description) {

        final var line = new SettlementModelDefinitionLine(
            this, step, participant, amountName, currency, direction, description);

        this.settlementModelDefinitionLines.add(line);

        return line;
    }

    @Override
    public SettlementModelDefinitionData convert() {

        final var lineData = this.settlementModelDefinitionLines
                                 .stream()
                                 .map(
                                     line -> new SettlementModelDefinitionData.SettlementModelDefinitionLineData(
                                         line.getId(), line.getStep(), line.getParticipant(),
                                         line.getAmountName(), line.getCurrency(),
                                         line.getDirection(), line.getDescription()))
                                 .toList();

        return new SettlementModelDefinitionData(
            this.id, this.scenario, this.currency, this.fspGroupId, this.transactionPeriod,
            this.name, this.description, this.settlementModel.getId(), this.activationStatus,
            this.terminationStatus, lineData);
    }

    public SettlementModelDefinition currency(final Currency currency) {

        Objects.requireNonNull(currency);

        this.currency = currency;

        return this;
    }

    public void deactivate() {

        this.activationStatus = ActivationStatus.INACTIVE;
    }

    public SettlementModelDefinition description(final String description) {

        if (description == null) {
            return this;
        }

        final var value = description.trim();

        if (value.length() > StringSizeConstraints.MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                "Settlement model definition description is too long.");
        }

        this.description = value;

        return this;
    }

    public SettlementModelDefinition fspGroupId(final FspGroupId fspGroupId) {

        Objects.requireNonNull(fspGroupId);

        this.fspGroupId = fspGroupId;

        return this;
    }

    @Override
    public SettlementDefinitionId getId() {

        return this.id;
    }

    public List<SettlementModelDefinitionLine> getSettlementModelDefinitionLines() {

        return Collections.unmodifiableList(this.settlementModelDefinitionLines);
    }

    public boolean matches(final ScenarioType scenario, final Currency currency,
                           final FspGroupId fspGroupId, final TransactionPeriod transactionPeriod) {

        return this.activationStatus == ActivationStatus.ACTIVE &&
                   this.terminationStatus == TerminationStatus.ALIVE && this.scenario == scenario &&
                   this.currency == currency && this.fspGroupId.equals(fspGroupId) &&
                   this.transactionPeriod == transactionPeriod;
    }

    public SettlementModelDefinition name(final String name) {

        Objects.requireNonNull(name);

        final var value = name.trim();

        if (value.isBlank()) {
            throw new IllegalArgumentException("Settlement model definition name is required.");
        }

        if (value.length() > StringSizeConstraints.MAX_NAME_TITLE_LENGTH) {
            throw new IllegalArgumentException("Settlement model definition name is too long.");
        }

        this.name = value;

        return this;
    }

    public SettlementModelDefinition scenario(final ScenarioType scenario) {

        Objects.requireNonNull(scenario);

        this.scenario = scenario;

        return this;
    }

    public SettlementModelDefinition settlementModel(final SettlementModel settlementModel) {

        Objects.requireNonNull(settlementModel);

        this.settlementModel = settlementModel;

        return this;
    }

    public void terminate() {

        this.terminationStatus = TerminationStatus.TERMINATED;
    }

    public SettlementModelDefinition transactionPeriod(final TransactionPeriod transactionPeriod) {

        Objects.requireNonNull(transactionPeriod);

        this.transactionPeriod = transactionPeriod;

        return this;
    }

}
