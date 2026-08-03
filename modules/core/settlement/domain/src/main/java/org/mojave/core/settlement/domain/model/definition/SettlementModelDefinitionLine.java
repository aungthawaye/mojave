package org.mojave.core.settlement.domain.model.definition;

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
import org.mojave.scheme.rule.converter.identifier.settlement.SettlementModelDefinitionLineIdJavaType;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.settlement.LiquidityDirection;
import org.mojave.scheme.rule.identifier.settlement.SettlementModelDefinitionLineId;

import java.util.Objects;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "stl_settlement_model_definition_line",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "stl_settlement_model_definition_line_01_UK",
            columnNames = {
                "definition_id",
                "participant",
                "amount_name",
                "currency",
                "direction"}),
        @UniqueConstraint(
            name = "stl_settlement_model_definition_line_02_UK",
            columnNames = {
                "definition_id",
                "step"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SettlementModelDefinitionLine extends JpaEntity<SettlementModelDefinitionLineId> {

    @Id
    @JavaType(SettlementModelDefinitionLineIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "settlement_model_definition_line_id",
        nullable = false,
        updatable = false)
    protected SettlementModelDefinitionLineId id;

    @Column(
        name = "participant",
        nullable = false,
        length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String participant;

    @Column(
        name = "amount_name",
        nullable = false,
        length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String amountName;

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

    @Column(
        name = "description",
        length = StringSizeConstraints.MAX_DESCRIPTION_LENGTH)
    protected String description;

    @Column(
        name = "step",
        nullable = false)
    protected Integer step = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
        name = "definition_id",
        nullable = false,
        foreignKey = @ForeignKey(
            name = "stl_settlement_model_definition_stl_settlement_model_definition_line_FK"))
    protected SettlementModelDefinition definition;

    public SettlementModelDefinitionLine(final SettlementModelDefinition definition,
                                         final Integer step, final String participant,
                                         final String amountName, final Currency currency,
                                         final LiquidityDirection direction,
                                         final String description) {

        Objects.requireNonNull(definition);

        this.id = new SettlementModelDefinitionLineId(Snowflake.get().nextId());
        this.definition = definition;

        this.forSettlementModelDefinitionLine(step, participant, amountName, currency, direction);
        this.description(description);
    }

    public SettlementModelDefinitionLine description(final String description) {

        if (description == null) {
            return this;
        }

        final var value = description.trim();

        if (value.length() > StringSizeConstraints.MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                "Settlement model definition line description is too long.");
        }

        this.description = value;

        return this;
    }

    public SettlementModelDefinitionLine forSettlementModelDefinitionLine(final Integer step,
                                                                          final String participant,
                                                                          final String amountName,
                                                                          final Currency currency,
                                                                          final LiquidityDirection direction) {

        Objects.requireNonNull(step);
        Objects.requireNonNull(participant);
        Objects.requireNonNull(amountName);
        Objects.requireNonNull(currency);
        Objects.requireNonNull(direction);

        if (this.definition.settlementModelDefinitionLines
                .stream()
                .anyMatch(line -> line.step.equals(step))) {
            throw new IllegalArgumentException(
                "Settlement model definition line step already exists.");
        }

        final var participantValue = participant.trim();
        final var amountNameValue = amountName.trim().toUpperCase();

        if (participantValue.isBlank()) {
            throw new IllegalArgumentException("Settlement participant is required.");
        }

        if (amountNameValue.isBlank()) {
            throw new IllegalArgumentException("Settlement amount name is required.");
        }

        this.step = step;
        this.participant = participantValue;
        this.amountName = amountNameValue;
        this.currency = currency;
        this.direction = direction;

        return this;
    }

    @Override
    public SettlementModelDefinitionLineId getId() {

        return this.id;
    }

}
