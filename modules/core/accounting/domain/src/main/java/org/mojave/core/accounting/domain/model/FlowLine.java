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
import org.mojave.common.datatype.converter.identifier.accounting.FlowLineIdJavaType;
import org.mojave.common.datatype.enums.accounting.PostingChannel;
import org.mojave.common.datatype.enums.accounting.Side;
import org.mojave.common.datatype.identifier.accounting.AccountId;
import org.mojave.common.datatype.identifier.accounting.CoaEntryId;
import org.mojave.common.datatype.identifier.accounting.FlowLineId;
import org.mojave.component.jpa.JpaEntity;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.core.accounting.contract.exception.definition.AccountConflictInDefinitionException;
import org.mojave.core.accounting.contract.exception.definition.AmbiguousReceiveInConfigException;
import org.mojave.core.accounting.contract.exception.definition.CoaEntryConflictInDefinitionException;
import org.mojave.core.accounting.contract.exception.definition.DefinitionDescriptionTooLongException;
import org.mojave.core.accounting.contract.exception.definition.DuplicateFlowLineIndexException;
import org.mojave.core.accounting.contract.exception.definition.ImmatureCoaEntryException;
import org.mojave.core.accounting.contract.exception.definition.InvalidAmountNameForTransactionTypeException;
import org.mojave.core.accounting.contract.exception.definition.InvalidParticipantForTransactionTypeException;
import org.mojave.core.accounting.contract.exception.definition.RequireParticipantForReceiveInException;
import org.mojave.core.accounting.domain.cache.AccountCache;
import org.mojave.core.accounting.domain.cache.CoaEntryCache;

import java.util.Objects;
import java.util.stream.Collectors;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "acc_flow_line",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "acc_flow_line_01_UK",
            columnNames = {
                "definition_id",
                "participant",
                "amount_name",
                "side",
                "posting_channel",
                "posting_channel_id"}),
        @UniqueConstraint(
            name = "acc_flow_line_02_UK",
            columnNames = {
                "definition_id",
                "step"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FlowLine extends JpaEntity<FlowLineId> {

    @Id
    @JavaType(FlowLineIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "flow_line_id",
        nullable = false)
    protected FlowLineId id;

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

    @Column(
        name = "posting_channel",
        nullable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected PostingChannel postingChannel;

    @Column(
        name = "posting_channel_id",
        nullable = false)
    protected Long postingChannelId;

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
        foreignKey = @ForeignKey(name = "acc_flow_definition_acc_flow_line_FK"))
    protected FlowDefinition definition;

    public FlowLine(FlowDefinition definition,
                    Integer step,
                    PostingChannel postingChannel,
                    Long postingChannelId,
                    String participant,
                    String amountName,
                    Side side,
                    String description,
                    AccountCache accountCache,
                    CoaEntryCache coaEntryCache) {

        Objects.requireNonNull(definition);
        Objects.requireNonNull(side);

        this.id = new FlowLineId(Snowflake.get().nextId());
        this.definition = definition;

        this.forFlowLine(
            step, postingChannel, postingChannelId, participant, amountName, side, accountCache,
            coaEntryCache).description(description);
    }

    public FlowLine description(String description) {

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

    public FlowLine forFlowLine(Integer step,
                                PostingChannel postingChannel,
                                Long receiveInId,
                                String participant,
                                String amountName,
                                Side side,
                                AccountCache accountCache,
                                CoaEntryCache coaEntryCache) {

        Objects.requireNonNull(step);
        Objects.requireNonNull(amountName);
        Objects.requireNonNull(side);
        Objects.requireNonNull(postingChannel);
        Objects.requireNonNull(receiveInId);
        Objects.requireNonNull(accountCache);
        Objects.requireNonNull(coaEntryCache);

        if (this.definition.flowLines.stream().anyMatch(flowLine -> flowLine.step.equals(step))) {
            throw new DuplicateFlowLineIndexException(step);
        }

        this.step = step;

        var _amountName = amountName.trim().toUpperCase();

        if (postingChannel == PostingChannel.ACCOUNT) {

            if (participant != null && participant.isBlank()) {

                throw new AmbiguousReceiveInConfigException();
            }
        }

        if (postingChannel == PostingChannel.CHART_ENTRY) {

            if (participant == null || participant.isBlank()) {

                throw new RequireParticipantForReceiveInException();
            }

            if (!this.definition.transactionType.getParticipants().types().contains(participant)) {

                throw new InvalidParticipantForTransactionTypeException(
                    this.definition.transactionType);
            }
        }

        // Validate that the flow line does not already exist for this flow definition.
        // First, check that the amount name/participant is valid for the flow definition's transaction type.
        if (!this.definition.transactionType.getAmounts().names().contains(amountName)) {

            throw new InvalidAmountNameForTransactionTypeException(this.definition.transactionType);
        }

        // Now verify whether the newly added flow line conflicts with any of the existing flow lines.
        // Here we need to verify these things:
        // 1. When CHART_ENTRY, any account of the adding CoaEntryId conflicts with any of the existing accounts or an account of the existing CoaEntryId.
        // 2. When ACCOUNT, the adding AccountId conflicts with any of the existing accounts or an account of the existing CoaEntryId.

        // Find all the accounts, created under the same postingChannelId in the accounting system, and previously added for the same Side and AmountName.
        final var existingAccountIds = this.definition.flowLines
                                        .stream()
                                        .filter(line -> line.postingChannel == PostingChannel.ACCOUNT &&
                                                            line.side == side &&
                                                            line.amountName.equals(_amountName))
                                        .map(line -> line.postingChannelId)
                                        .collect(Collectors.toSet());

        final var existingChartEntryIds = this.definition.flowLines
                                           .stream()
                                           .filter(
                                               line -> line.postingChannel == PostingChannel.CHART_ENTRY &&
                                                           line.participant.equals(participant) &&
                                                           line.side == side &&
                                                           line.amountName.equals(_amountName))
                                           .map(line -> line.postingChannelId)
                                           .collect(Collectors.toSet());

        if (postingChannel == PostingChannel.CHART_ENTRY) {

            var coaEntryId = new CoaEntryId(receiveInId);

            if (existingChartEntryIds.contains(coaEntryId.getId())) {
                final var coaEntryData = coaEntryCache.get(coaEntryId);
                // There is the same flow line for the same coaEntryId, participant, side and amountName.
                throw new CoaEntryConflictInDefinitionException(coaEntryData.code());
            }

            final var accounts = accountCache.get(coaEntryId);

            if (accounts == null || accounts.isEmpty()) {

                var coaEntry = coaEntryCache.get(coaEntryId);
                throw new ImmatureCoaEntryException(coaEntry.code());

            } else {

                accounts
                    .stream()
                    .filter(account -> existingAccountIds.contains(account.accountId().getId()))
                    .findFirst()
                    .ifPresent((conflict) -> {
                        throw new AccountConflictInDefinitionException(conflict.code());
                    });
            }

        } else {

            final var _accountId = new AccountId(receiveInId);
            final var accountData = accountCache.get(_accountId);

            if (existingAccountIds.contains(_accountId.getId())) {
                // There is the same flow line for the same AccountId, side and amountName.
                throw new AccountConflictInDefinitionException(accountData.code());
            }

            // Then, make sure this AccountId won't conflict with any other flow line configured with
            // BY_CHART_ENTRY for the same side and amountName.
            // In this case, we need to check using the accounts of each CoaEntryId which are already
            // added to the definition.
            for (var existingChartEntryId : existingChartEntryIds) {

                final var accounts = accountCache.get(new CoaEntryId(existingChartEntryId));

                accounts
                    .stream()
                    .filter(account -> account.accountId().equals(_accountId))
                    .findFirst()
                    .ifPresent((conflict) -> {
                        throw new AccountConflictInDefinitionException(conflict.code());
                    });
            }
        }

        this.participant = participant;
        this.amountName = _amountName;
        this.side = side;
        this.postingChannel = postingChannel;
        this.postingChannelId = receiveInId;

        return this;
    }

}
