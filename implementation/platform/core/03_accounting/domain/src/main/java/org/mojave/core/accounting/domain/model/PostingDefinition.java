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
import org.mojave.component.jpa.JpaEntity;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.core.accounting.contract.exception.definition.AccountConflictInDefinitionException;
import org.mojave.core.accounting.contract.exception.definition.AmbiguousReceiveInConfigException;
import org.mojave.core.accounting.contract.exception.definition.ChartEntryConflictInDefinitionException;
import org.mojave.core.accounting.contract.exception.definition.DefinitionDescriptionTooLongException;
import org.mojave.core.accounting.contract.exception.definition.DuplicatePostingDefinitionIndexException;
import org.mojave.core.accounting.contract.exception.definition.ImmatureChartEntryException;
import org.mojave.core.accounting.contract.exception.definition.InvalidAmountNameForTransactionTypeException;
import org.mojave.core.accounting.contract.exception.definition.InvalidParticipantForTransactionTypeException;
import org.mojave.core.accounting.contract.exception.definition.RequireParticipantForReceiveInException;
import org.mojave.core.accounting.domain.cache.AccountCache;
import org.mojave.core.accounting.domain.cache.ChartEntryCache;
import org.mojave.core.common.datatype.converter.identifier.accounting.PostingDefinitionIdJavaType;
import org.mojave.core.common.datatype.enums.accounting.ReceiveIn;
import org.mojave.core.common.datatype.enums.accounting.Side;
import org.mojave.core.common.datatype.identifier.accounting.AccountId;
import org.mojave.core.common.datatype.identifier.accounting.ChartEntryId;
import org.mojave.core.common.datatype.identifier.accounting.PostingDefinitionId;

import java.util.stream.Collectors;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "acc_posting_definition",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "acc_posting_definition_for_posting_UK",
            columnNames = {
                "definition_id",
                "participant",
                "amount_name",
                "side",
                "receive_in",
                "receive_in_id"}),
        @UniqueConstraint(
            name = "acc_posting_definition_definition_id_step_UK",
            columnNames = {
                "definition_id",
                "step"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostingDefinition extends JpaEntity<PostingDefinitionId> {

    @Id
    @JavaType(PostingDefinitionIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "posting_definition_id",
        nullable = false)
    protected PostingDefinitionId id;

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
        name = "receive_in",
        nullable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected ReceiveIn receiveIn;

    @Column(
        name = "receive_in_id",
        nullable = false)
    protected Long receiveInId;

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
        foreignKey = @ForeignKey(name = "acc_posting_definition_acc_flow_definition_FK"))
    protected FlowDefinition definition;

    public PostingDefinition(FlowDefinition definition,
                             Integer step,
                             ReceiveIn receiveIn,
                             Long receiveInId,
                             String participant,
                             String amountName,
                             Side side,
                             String description,
                             AccountCache accountCache,
                             ChartEntryCache chartEntryCache) {

        assert definition != null;
        assert side != null;

        this.id = new PostingDefinitionId(Snowflake.get().nextId());
        this.definition = definition;
        this.forPosting(
            step, receiveIn, receiveInId, participant, amountName, side, accountCache,
            chartEntryCache).description(description);
    }

    public PostingDefinition description(String description) {

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

    public PostingDefinition forPosting(Integer step,
                                        ReceiveIn receiveIn,
                                        Long receiveInId,
                                        String participant,
                                        String amountName,
                                        Side side,
                                        AccountCache accountCache,
                                        ChartEntryCache chartEntryCache) {

        assert step != null;
        assert amountName != null;
        assert side != null;
        assert receiveIn != null;
        assert receiveInId != null;
        assert accountCache != null;
        assert chartEntryCache != null;

        if (this.definition.postings.stream().anyMatch(pd -> pd.step.equals(step))) {
            throw new DuplicatePostingDefinitionIndexException(step);
        }

        this.step = step;

        var _amountName = amountName.trim().toUpperCase();

        if (receiveIn == ReceiveIn.ACCOUNT) {

            if (participant != null && participant.isBlank()) {

                throw new AmbiguousReceiveInConfigException();
            }
        }

        if (receiveIn == ReceiveIn.CHART_ENTRY) {

            if (participant == null || participant.isBlank()) {

                throw new RequireParticipantForReceiveInException();
            }

            if (!this.definition.transactionType.getParticipants().types().contains(participant)) {

                throw new InvalidParticipantForTransactionTypeException(
                    this.definition.transactionType);
            }
        }

        // Validate that the posting definition does not already exist for this flow definition.
        // First, check that the amount name/participant is valid for the flow definition's transaction type.
        if (!this.definition.transactionType.getAmounts().names().contains(amountName)) {

            throw new InvalidAmountNameForTransactionTypeException(this.definition.transactionType);
        }

        // Now verify whether the newly adding posting conflicts with any of the existing posting definition.
        // Here we need to verify these things:
        // 1. When CHART_ENTRY, any account of the adding ChartEntryId conflicts with any of the existing accounts or an account of the existing ChartEntryId.
        // 2. When ACCOUNT, the adding AccountId conflicts with any of the existing accounts or an account of the existing ChartEntryId.

        // Find all the accounts, created under the same receiveInId in the accounting system, and previously added for the same Side and AmountName.
        var existingAccountIds = this.definition.postings
                                     .stream()
                                     .filter(pd -> pd.receiveIn == ReceiveIn.ACCOUNT &&
                                                       pd.side == side &&
                                                       pd.amountName.equals(_amountName))
                                     .map(pd -> pd.receiveInId)
                                     .collect(Collectors.toSet());

        var existingChartEntryIds = this.definition.postings
                                        .stream()
                                        .filter(pd -> pd.receiveIn == ReceiveIn.CHART_ENTRY &&
                                                          pd.participant.equals(participant) &&
                                                          pd.side == side &&
                                                          pd.amountName.equals(_amountName))
                                        .map(pd -> pd.receiveInId)
                                        .collect(Collectors.toSet());

        if (receiveIn == ReceiveIn.CHART_ENTRY) {

            var _chartEntryId = new ChartEntryId(receiveInId);

            if (existingChartEntryIds.contains(_chartEntryId.getId())) {
                var chartEntryData = chartEntryCache.get(_chartEntryId);
                // There is the same Posting Definition for the same chartEntryId, participant, side and amountName.
                throw new ChartEntryConflictInDefinitionException(chartEntryData.code());
            }

            var accounts = accountCache.get(_chartEntryId);

            if (accounts == null || accounts.isEmpty()) {

                var _chartEntry = chartEntryCache.get(_chartEntryId);
                throw new ImmatureChartEntryException(_chartEntry.code());

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

            var _accountId = new AccountId(receiveInId);
            var accountData = accountCache.get(_accountId);

            if (existingAccountIds.contains(_accountId.getId())) {
                // There is the same Posting Definition for the same AccountId, side and amountName.
                throw new AccountConflictInDefinitionException(accountData.code());
            }

            // Then, make sure this AccountId won't conflict with any other Posting Definition configured with
            // BY_CHART_ENTRY for the same side and amountName.
            // In this case, we need to check using the accounts of each ChartEntryId which are already
            // added to the definition.
            for (var existingChartEntryId : existingChartEntryIds) {

                var accounts = accountCache.get(new ChartEntryId(existingChartEntryId));

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
        this.receiveIn = receiveIn;
        this.receiveInId = receiveInId;

        return this;
    }

}
