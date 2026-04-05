package org.mojave.core.accounting.domain.model;

import org.junit.jupiter.api.Test;
import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.TerminationStatus;
import org.mojave.common.datatype.enums.accounting.AccountType;
import org.mojave.common.datatype.enums.accounting.ChartEntryCategory;
import org.mojave.common.datatype.enums.accounting.Side;
import org.mojave.common.datatype.identifier.accounting.AccountId;
import org.mojave.common.datatype.identifier.accounting.AccountOwnerId;
import org.mojave.common.datatype.identifier.accounting.CoaEntryId;
import org.mojave.common.datatype.identifier.accounting.CoaId;
import org.mojave.common.datatype.type.accounting.AccountCode;
import org.mojave.common.datatype.type.accounting.CoaEntryCode;
import org.mojave.core.accounting.contract.data.AccountData;
import org.mojave.core.accounting.contract.data.CoaEntryData;
import org.mojave.core.accounting.domain.cache.AccountCache;
import org.mojave.core.accounting.domain.cache.CoaEntryCache;
import org.mojave.core.scheme.rule.data.TransactionTypeDefinitionData;
import org.mojave.core.scheme.rule.dimension.FundTransferDimension;
import org.mojave.core.scheme.rule.type.TransactionType;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlowLineUT {

    @Test
    public void shouldNormalizeAmountNameBeforeValidation() {

        final var definition = new FlowDefinition(
            TransactionType.FUND_TRANSFER, Currency.USD, "fund-transfer", null);
        final var coaEntryId = new CoaEntryId(1L);
        final var accountCache = new TestAccountCache(coaEntryId);
        final var coaEntryCache = new TestCoaEntryCache(coaEntryId);
        final var transactionTypeDefinition = new TransactionTypeDefinitionData(
            TransactionType.FUND_TRANSFER,
            Set.of(
                FundTransferDimension.Participants.PAYER_FSP.name(),
                FundTransferDimension.Participants.PAYEE_FSP.name()),
            Set.of(
                FundTransferDimension.Amounts.TRANSFER_AMOUNT.name(),
                FundTransferDimension.Amounts.PAYEE_FSP_FEE.name(),
                FundTransferDimension.Amounts.PAYEE_FSP_COMMISSION.name()));

        final var flowLine = assertDoesNotThrow(() -> definition.addFlowLine(
            1, FundTransferDimension.Participants.PAYER_FSP.name(), coaEntryId, " transfer_amount ",
            Side.DEBIT, null, transactionTypeDefinition, accountCache, coaEntryCache));

        assertEquals(
            FundTransferDimension.Amounts.TRANSFER_AMOUNT.name(), flowLine.getAmountName());
    }

    private static final class TestAccountCache implements AccountCache {

        private final CoaEntryId coaEntryId;

        private TestAccountCache(final CoaEntryId coaEntryId) {

            this.coaEntryId = coaEntryId;
        }

        @Override
        public void clear() {

        }

        @Override
        public void delete(final AccountId accountId) {

        }

        @Override
        public AccountData get(final AccountCode accountCode) {

            return null;
        }

        @Override
        public Set<AccountData> get(final AccountOwnerId ownerId) {

            return Set.of();
        }

        @Override
        public AccountData get(final AccountId accountId) {

            return null;
        }

        @Override
        public AccountData get(final CoaEntryId coaEntryId,
                               final AccountOwnerId ownerId,
                               final Currency currency) {

            return null;
        }

        @Override
        public Set<AccountData> get(final CoaEntryId coaEntryId) {

            if (!this.coaEntryId.equals(coaEntryId)) {
                return Set.of();
            }

            return Set.of(new AccountData(
                new AccountId(10L), new AccountOwnerId(20L), AccountType.ASSET, Currency.USD,
                new AccountCode("ACC-1"), "Test Account", null, Instant.now(),
                ActivationStatus.ACTIVE, TerminationStatus.ALIVE, coaEntryId));
        }

        @Override
        public void save(final AccountData account) {

        }

    }

    private static final class TestCoaEntryCache implements CoaEntryCache {

        private final CoaEntryId coaEntryId;

        private TestCoaEntryCache(final CoaEntryId coaEntryId) {

            this.coaEntryId = coaEntryId;
        }

        @Override
        public void clear() {

        }

        @Override
        public void delete(final CoaEntryId coaEntryId) {

        }

        @Override
        public CoaEntryData get(final CoaEntryId coaEntryId) {

            if (!this.coaEntryId.equals(coaEntryId)) {
                return null;
            }

            return new CoaEntryData(
                coaEntryId, ChartEntryCategory.FSP, new CoaEntryCode("COA-1"), "Test CoA Entry",
                null, AccountType.ASSET, Instant.now(), new CoaId(30L));
        }

        @Override
        public CoaEntryData get(final CoaEntryCode code) {

            return null;
        }

        @Override
        public Set<CoaEntryData> get(final CoaId coaId) {

            return Set.of();
        }

        @Override
        public void save(final CoaEntryData coaEntry) {

        }

    }

}
