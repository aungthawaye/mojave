package io.mojaloop.core.accounting.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.accounting.contract.exception.definition.AccountConflictsInPostingDefinitionException;
import io.mojaloop.core.accounting.contract.exception.definition.ChartEntryConflictsInPostingDefinitionException;
import io.mojaloop.core.accounting.contract.exception.definition.DefinitionDescriptionTooLongException;
import io.mojaloop.core.accounting.contract.exception.definition.ImmatureChartEntryException;
import io.mojaloop.core.accounting.contract.exception.definition.InvalidAmountNameForTransactionTypeException;
import io.mojaloop.core.accounting.contract.exception.definition.PostingDefinitionAlreadyExistsException;
import io.mojaloop.core.accounting.domain.cache.AccountCache;
import io.mojaloop.core.accounting.domain.cache.ChartEntryCache;
import io.mojaloop.core.common.datatype.converter.identifier.accounting.PostingDefinitionIdJavaType;
import io.mojaloop.core.common.datatype.enums.accounting.AccountResolving;
import io.mojaloop.core.common.datatype.enums.accounting.Side;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.accounting.PostingDefinitionId;
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

import java.util.stream.Collectors;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(name = "acc_posting_definition",
       uniqueConstraints = {@UniqueConstraint(name = "acc_posting_definition_for_posting_UK", columnNames = {"definition_id", "participant_type", "amount_name", "side", "account_resolving", "account_or_chart_entry_id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostingDefinition extends JpaEntity<PostingDefinitionId> {

    @Id
    @JavaType(PostingDefinitionIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "posting_definition_id", nullable = false)
    protected PostingDefinitionId id;

    @Column(name = "participant_type", nullable = false, length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String participantType;

    @Column(name = "amount_name", nullable = false, length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String amountName;

    @Column(name = "side", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Side side;

    @Column(name = "account_resolving", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected AccountResolving accountResolving;

    @Column(name = "account_or_chart_entry_id", nullable = false)
    protected Long accountOrChartEntryId;

    @Column(name = "description", length = StringSizeConstraints.MAX_DESCRIPTION_LENGTH)
    protected String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "definition_id", nullable = false, foreignKey = @ForeignKey(name = "acc_posting_definition_acc_flow_definition_FK"))
    protected FlowDefinition definition;

    public PostingDefinition(FlowDefinition definition,
                             String participantType,
                             String amountName,
                             Side side,
                             AccountResolving accountResolving,
                             Long accountOrChartEntryId,
                             String description,
                             AccountCache accountCache,
                             ChartEntryCache chartEntryCache) {

        assert definition != null;
        assert side != null;

        this.id = new PostingDefinitionId(Snowflake.get().nextId());
        this.definition = definition;
        this.forPosting(participantType, amountName, side, accountResolving, accountOrChartEntryId, accountCache, chartEntryCache).description(description);
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

    public PostingDefinition forPosting(String participantType,
                                        String amountName,
                                        Side side,
                                        AccountResolving accountResolving,
                                        Long accountOrChartEntryId,
                                        AccountCache accountCache,
                                        ChartEntryCache chartEntryCache) {

        assert participantType != null;
        assert amountName != null;
        assert side != null;
        assert accountResolving != null;
        assert accountOrChartEntryId != null;
        assert accountCache != null;
        assert chartEntryCache != null;

        var _amountName = amountName.trim().toUpperCase();

        // Validate that the posting definition does not already exist for this flow definition.
        // First, check that the amount name is valid for the flow definition's transaction type.
        if (!this.definition.transactionType.getAmounts().names().contains(amountName)) {

            throw new InvalidAmountNameForTransactionTypeException(this.definition.transactionType);
        }

        // Now verify whether the newly adding posting conflicts with any of the existing posting definition.
        // Here we need to verify these things:
        // 1. When BY_CHART_ENTRY, an account of the adding ChartEntryId conflicts with any of the existing accounts or an account of the existing ChartEntryId.
        // 2. When BY_ACCOUNT, the adding AccountId conflicts with any of the existing accounts or an account of the existing ChartEntryId.

        // Find all the accounts, created under the same accountOrChartEntryId in the accounting system, and previously added for the same Side and AmountName.
        var existingAccountIds = this.definition.postingDefinitions.stream()
                                                                   .filter(pd -> pd.participantType.equals(participantType) && pd.accountResolving == AccountResolving.BY_ACCOUNT &&
                                                                                     pd.side == side && pd.amountName.equals(_amountName))
                                                                   .map(pd -> pd.accountOrChartEntryId)
                                                                   .collect(Collectors.toSet());

        var existingChartEntryIds = this.definition.postingDefinitions.stream()
                                                                      .filter(pd -> pd.participantType.equals(participantType) &&
                                                                                        pd.accountResolving == AccountResolving.BY_CHART_ENTRY && pd.side == side &&
                                                                                        pd.amountName.equals(_amountName))
                                                                      .map(pd -> pd.accountOrChartEntryId)
                                                                      .collect(Collectors.toSet());

        if (accountResolving == AccountResolving.BY_CHART_ENTRY) {

            if (existingChartEntryIds.contains(accountOrChartEntryId)) {

                // There is the same Posting Definition for the same ChartEntryId, side and amountName.
                throw new PostingDefinitionAlreadyExistsException();
            }

            var accountsOfChartEntry = accountCache.get(new ChartEntryId(accountOrChartEntryId));

            if (accountsOfChartEntry == null || accountsOfChartEntry.isEmpty()) {

                throw new ImmatureChartEntryException();

            } else {

                var optConflictingAccount = accountsOfChartEntry.stream().filter(account -> existingAccountIds.contains(account.accountId().getId())).findFirst();

                if (optConflictingAccount.isPresent()) {

                    var conflictingAccount = optConflictingAccount.get();
                    var chartEntry = chartEntryCache.get(conflictingAccount.chartEntryId());

                    throw new ChartEntryConflictsInPostingDefinitionException(conflictingAccount.code().value(), chartEntry.name(), side, amountName);

                }
            }

        } else {

            if (existingAccountIds.contains(accountOrChartEntryId)) {

                // There is the same Posting Definition for the same AccountId, side and amountName.
                throw new PostingDefinitionAlreadyExistsException();
            }

            // Then, make sure this AccountId won't conflict with any other Posting Definition configured with BY_CHART_ENTRY for the same side and amountName.
            // In this case, we need to check using the accounts of the ChartEntryId.

            for (var existingChartEntryId : existingChartEntryIds) {

                var accountsOfChartEntry = accountCache.get(new ChartEntryId(existingChartEntryId));

                var optConflictingAccount = accountsOfChartEntry.stream().filter(account -> account.accountId().getId().equals(accountOrChartEntryId)).findFirst();

                if (optConflictingAccount.isPresent()) {

                    var conflictingAccount = optConflictingAccount.get();

                    throw new AccountConflictsInPostingDefinitionException(conflictingAccount.name(), side, amountName);
                }
            }
        }

        this.participantType = participantType;
        this.amountName = _amountName;
        this.side = side;
        this.accountResolving = accountResolving;
        this.accountOrChartEntryId = accountOrChartEntryId;

        return this;
    }

}