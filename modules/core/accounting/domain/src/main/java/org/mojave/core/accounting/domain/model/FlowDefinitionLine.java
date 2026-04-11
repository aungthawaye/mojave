/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===
 */

package org.mojave.core.accounting.domain.model;

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
import org.mojave.core.accounting.contract.exception.chart.CoaEntryIdNotFoundException;
import org.mojave.core.accounting.contract.exception.definition.CoaEntryConflictInDefinitionException;
import org.mojave.core.accounting.contract.exception.definition.DefinitionDescriptionTooLongException;
import org.mojave.core.accounting.contract.exception.definition.DuplicateFlowDefinitionLineIndexException;
import org.mojave.core.accounting.contract.exception.definition.ImmatureCoaEntryException;
import org.mojave.core.accounting.contract.exception.definition.InvalidAmountNameForAccountingScenarioException;
import org.mojave.core.accounting.contract.exception.definition.InvalidParticipantForAccountingScenarioException;
import org.mojave.core.accounting.contract.exception.definition.RequireParticipantForCoaEntryException;
import org.mojave.core.accounting.domain.cache.AccountCache;
import org.mojave.core.accounting.domain.cache.CoaEntryCache;
import org.mojave.common.datatype.converter.identifier.accounting.CoaEntryIdJavaType;
import org.mojave.common.datatype.converter.identifier.accounting.FlowDefinitionLineIdJavaType;
import org.mojave.common.datatype.enums.accounting.Side;
import org.mojave.common.datatype.identifier.accounting.CoaEntryId;
import org.mojave.common.datatype.identifier.accounting.FlowDefinitionLineId;
import org.mojave.component.jpa.JpaEntity;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.scheme.rule.scenario.ScenarioDefinitions;

import java.util.Objects;
import java.util.stream.Collectors;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "acc_flow_definition_line",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "acc_flow_definition_line_01_UK",
            columnNames = {
                "definition_id",
                "participant",
                "amount_name",
                "side",
                "coa_entry_id"}),
        @UniqueConstraint(
            name = "acc_flow_definition_line_02_UK",
            columnNames = {
                "definition_id",
                "step"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FlowDefinitionLine extends JpaEntity<FlowDefinitionLineId> {

    @Id
    @JavaType(FlowDefinitionLineIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "flow_definition_line_id",
        nullable = false)
    protected FlowDefinitionLineId id;

    @Column(
        name = "participant",
        length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String participant;

    @Column(
        name = "amount_name",
        nullable = false,
        length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String amountName;

    @Column(
        name = "side",
        nullable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Side side;

    @JavaType(CoaEntryIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "coa_entry_id",
        nullable = false)
    protected CoaEntryId coaEntryId;

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
        foreignKey = @ForeignKey(name = "acc_flow_definition_acc_flow_definition_line_FK"))
    protected FlowDefinition definition;

    public FlowDefinitionLine(FlowDefinition definition, Integer step, String participant,
                    CoaEntryId coaEntryId, String amountName, Side side, String description,
                    AccountCache accountCache, CoaEntryCache coaEntryCache) {

        Objects.requireNonNull(definition);
        Objects.requireNonNull(side);

        this.id = new FlowDefinitionLineId(Snowflake.get().nextId());
        this.definition = definition;

        this
            .forFlowDefinitionLine(
                step, participant, coaEntryId, amountName, side, accountCache,
                coaEntryCache)
            .description(description);
    }

    public FlowDefinitionLine description(String description) {

        if (description == null) {
            return this;
        }

        var value = description.trim();

        if (value.length() > StringSizeConstraints.MAX_DESCRIPTION_LENGTH) {
            throw new DefinitionDescriptionTooLongException();
        }

        this.description = description;

        return this;
    }

    public FlowDefinitionLine forFlowDefinitionLine(Integer step, String participant, CoaEntryId coaEntryId,
                                String amountName, Side side, AccountCache accountCache,
                                CoaEntryCache coaEntryCache) {

        var scenarioDefinition = ScenarioDefinitions.get(this.definition.scenario);

        Objects.requireNonNull(step);
        Objects.requireNonNull(amountName);
        Objects.requireNonNull(side);
        Objects.requireNonNull(coaEntryId);
        Objects.requireNonNull(accountCache);
        Objects.requireNonNull(coaEntryCache);

        if (this.definition.flowDefinitionLines.stream().anyMatch(flowDefinitionLine -> flowDefinitionLine.step.equals(step))) {
            throw new DuplicateFlowDefinitionLineIndexException(step);
        }

        this.step = step;

        final var _amountName = amountName.trim().toUpperCase();
        final var _participant = participant == null ? null : participant.trim();

        if (_participant == null || _participant.isBlank()) {

            throw new RequireParticipantForCoaEntryException();
        }

        if (!scenarioDefinition.containsParticipant(_participant)) {

            throw new InvalidParticipantForAccountingScenarioException(
                this.definition.scenario, scenarioDefinition.participants());
        }

        // Validate that the flow definition line does not already exist for this flow definition.
        // First, check that the amount name/participant is valid for the flow definition's transaction type.
        if (!scenarioDefinition.containsAmountName(_amountName)) {

            throw new InvalidAmountNameForAccountingScenarioException(
                _amountName, this.definition.scenario, scenarioDefinition.amounts());
        }

        final var coaEntryData = coaEntryCache.get(coaEntryId);

        if (coaEntryData == null) {
            throw new CoaEntryIdNotFoundException(coaEntryId);
        }

        final var existingCoaEntryIds = this.definition.flowDefinitionLines
                                            .stream()
                                            .filter(line -> line.participant.equals(_participant) &&
                                                                line.side == side &&
                                                                line.amountName.equals(_amountName))
                                            .map(line -> line.coaEntryId)
                                            .collect(Collectors.toSet());

        if (existingCoaEntryIds.contains(coaEntryId)) {
            throw new CoaEntryConflictInDefinitionException(coaEntryData.code());
        }

        final var accounts = accountCache.get(coaEntryId);

        if (accounts == null || accounts.isEmpty()) {
            throw new ImmatureCoaEntryException(coaEntryData.code());
        }

        this.participant = _participant;
        this.amountName = _amountName;
        this.side = side;
        this.coaEntryId = coaEntryId;

        return this;
    }

}
