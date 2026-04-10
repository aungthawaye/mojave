package org.mojave.accounting.domain.command.ledger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.accounting.contract.command.account.CreateAccountCommand;
import org.mojave.accounting.contract.command.account.DeactivateAccountCommand;
import org.mojave.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.accounting.contract.command.chart.CreateCoaEntryCommand;
import org.mojave.accounting.contract.command.definition.CreateFlowDefinitionCommand;
import org.mojave.accounting.contract.command.definition.DeactivateFlowDefinitionCommand;
import org.mojave.accounting.contract.command.definition.TerminateFlowDefinitionCommand;
import org.mojave.accounting.contract.command.ledger.PostAccountingFlowCommand;
import org.mojave.accounting.contract.exception.account.AccountNotActiveException;
import org.mojave.accounting.contract.exception.definition.FlowDefinitionNotConfiguredException;
import org.mojave.accounting.contract.exception.ledger.DuplicatePostingInLedgerException;
import org.mojave.accounting.contract.exception.ledger.InsufficientBalanceInAccountException;
import org.mojave.accounting.contract.exception.ledger.OverdraftLimitReachedInAccountException;
import org.mojave.accounting.contract.exception.ledger.PostingAccountNotFoundException;
import org.mojave.accounting.contract.exception.ledger.RequiredAmountNameNotFoundInTransactionException;
import org.mojave.accounting.contract.exception.ledger.RequiredParticipantNotFoundInTransactionException;
import org.mojave.accounting.contract.exception.ledger.RestoreFailedInAccountException;
import org.mojave.accounting.domain.AccountingDomainTestConfiguration;
import org.mojave.accounting.domain.BaseIT;
import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.TerminationStatus;
import org.mojave.common.datatype.enums.accounting.AccountType;
import org.mojave.common.datatype.enums.accounting.OverdraftMode;
import org.mojave.common.datatype.enums.accounting.Side;
import org.mojave.common.datatype.identifier.accounting.AccountId;
import org.mojave.common.datatype.identifier.accounting.AccountOwnerId;
import org.mojave.common.datatype.identifier.accounting.FlowDefinitionId;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.accounting.scenario.AccountingScenario;
import org.mojave.scheme.rule.accounting.scenario.dimension.FundTransferFlowDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        AccountingDomainTestConfiguration.class})
@DisplayName("Post LedgerOperation Flow Command Integration Test")
public class PostLedgerEngineFlowCommandIT extends BaseIT {

    @Autowired
    private CreateAccountCommand createAccountCommand;

    @Autowired
    private CreateCoaCommand createCoaCommand;

    @Autowired
    private CreateCoaEntryCommand createCoaEntryCommand;

    @Autowired
    private CreateFlowDefinitionCommand createFlowDefinitionCommand;

    @Autowired
    private DeactivateAccountCommand deactivateAccountCommand;

    @Autowired
    private DeactivateFlowDefinitionCommand deactivateFlowDefinitionCommand;

    @Autowired
    private PostAccountingFlowCommand postAccountingFlowCommand;

    @Autowired
    private TerminateFlowDefinitionCommand terminateFlowDefinitionCommand;

    @Test
    @DisplayName("Throw when posting account is not active")
    public void accountNotActive() {

        final var fixture = this.createTransferPostingFixture(
            "POST_LEDGER_ACCOUNT_INACTIVE", Currency.USD);

        this.deactivateAccountCommand.execute(
            new DeactivateAccountCommand.Input(fixture.payeeAccountId()));

        final var exception = assertThrows(
            AccountNotActiveException.class,
            () -> this.postAccountingFlowCommand.execute(this.buildTransferInput(
                fixture, new TransactionId(908L),
                Map.of("PAYER_FSP", fixture.payerOwnerId(), "PAYEE_FSP", fixture.payeeOwnerId()),
                Map.of(
                    "TRANSFER_AMOUNT", new BigDecimal("100.00"), "PAYEE_FSP_FEE",
                    new BigDecimal("4.00")))));

        assertEquals(this.code(fixture.prefix(), "ACC02"), exception.extras().get("accountCode"));
    }

    @Test
    @DisplayName("Map duplicate posting error from ledger")
    public void duplicatePostingInLedger() throws Exception {

        final var fixture = this.createSingleCreditPostingFixture(
            "POST_LEDGER_DUPLICATE", Currency.USD);
        final var transactionId = new TransactionId(912L);
        final var input = this.buildTransferInput(
            null, transactionId,
            Map.of("PAYEE_FSP", fixture.payeeOwnerId()),
            Map.of("TRANSFER_AMOUNT", new BigDecimal("100.00")));

        this.postAccountingFlowCommand.execute(input);

        final var exception = assertThrows(
            DuplicatePostingInLedgerException.class,
            () -> this.postAccountingFlowCommand.execute(input));

        assertEquals(this.code(fixture.prefix(), "ACC01"), exception.extras().get("accountCode"));
    }

    @Test
    @DisplayName("Throw when flow definition is not configured")
    public void flowDefinitionNotConfigured() {

        final var input = this.buildTransferInput(
            new PostingFixture(
                null, null, null, null, new AccountOwnerId(1L),
                new AccountOwnerId(2L)), new TransactionId(902L),
            Map.of("PAYER_FSP", new AccountOwnerId(1L), "PAYEE_FSP", new AccountOwnerId(2L)),
            Map.of(
                "TRANSFER_AMOUNT", new BigDecimal("100.00"), "PAYEE_FSP_FEE",
                new BigDecimal("4.00")));

        assertThrows(
            FlowDefinitionNotConfiguredException.class,
            () -> this.postAccountingFlowCommand.execute(input));
    }

    @Test
    @DisplayName("Throw when flow definition is inactive")
    public void inactiveFlowDefinition() {

        final var fixture = this.createTransferPostingFixture("POST_LEDGER_INACTIVE", Currency.USD);

        this.deactivateFlowDefinitionCommand.execute(
            new DeactivateFlowDefinitionCommand.Input(fixture.flowDefinitionId()));

        assertThrows(
            FlowDefinitionNotConfiguredException.class,
            () -> this.postAccountingFlowCommand.execute(this.buildTransferInput(
                fixture, new TransactionId(903L),
                Map.of("PAYER_FSP", fixture.payerOwnerId(), "PAYEE_FSP", fixture.payeeOwnerId()),
                Map.of(
                    "TRANSFER_AMOUNT", new BigDecimal("100.00"), "PAYEE_FSP_FEE",
                    new BigDecimal("4.00")))));
    }

    @Test
    @DisplayName("Map insufficient balance failure from ledger")
    public void insufficientBalanceInAccount() {

        final var fixture = this.createSingleAssetCreditPostingFixture(
            "POST_LEDGER_INSUFFICIENT", Currency.USD);
        final var transactionId = new TransactionId(909L);

        final var exception = assertThrows(
            InsufficientBalanceInAccountException.class,
            () -> this.postAccountingFlowCommand.execute(this.buildTransferInput(
                fixture, transactionId, Map.of("PAYER_FSP", fixture.payerOwnerId()),
                Map.of("TRANSFER_AMOUNT", new BigDecimal("100.00")))));

        assertEquals(this.code(fixture.prefix(), "ACC01"), exception.extras().get("accountCode"));
    }

    @Test
    @DisplayName("Map overdraft limit failure from ledger")
    public void overdraftLimitReachedInAccount() {

        final var fixture = this.createSingleAssetCreditPostingFixture(
            "POST_LEDGER_OVERDRAFT",
            Currency.USD, OverdraftMode.LIMITED, new BigDecimal("10.00"));
        final var transactionId = new TransactionId(910L);

        final var exception = assertThrows(
            OverdraftLimitReachedInAccountException.class,
            () -> this.postAccountingFlowCommand.execute(this.buildTransferInput(
                fixture, transactionId, Map.of("PAYER_FSP", fixture.payerOwnerId()),
                Map.of("TRANSFER_AMOUNT", new BigDecimal("100.00")))));

        assertEquals(this.code(fixture.prefix(), "ACC01"), exception.extras().get("accountCode"));
    }

    @Test
    @DisplayName("Throw when posting account cannot be found")
    public void postingAccountNotFound() {

        final var fixture = this.createTransferPostingFixture(
            "POST_LEDGER_ACCOUNT_MISSING", Currency.USD);

        final var exception = assertThrows(
            PostingAccountNotFoundException.class,
            () -> this.postAccountingFlowCommand.execute(this.buildTransferInput(
                fixture, new TransactionId(907L),
                Map.of(
                    "PAYER_FSP", fixture.payerOwnerId(), "PAYEE_FSP",
                    new AccountOwnerId(Long.MAX_VALUE)),
                Map.of(
                    "TRANSFER_AMOUNT", new BigDecimal("100.00"), "PAYEE_FSP_FEE",
                    new BigDecimal("4.00")))));

        assertEquals(Long.toString(Long.MAX_VALUE), exception.extras().get("ownerId"));
    }

    @Test
    @DisplayName("Throw when required amount is missing")
    public void requiredAmountNameNotFoundInTransaction() {

        final var fixture = this.createTransferPostingFixture(
            "POST_LEDGER_AMOUNT_MISSING", Currency.USD);

        assertThrows(
            RequiredAmountNameNotFoundInTransactionException.class,
            () -> this.postAccountingFlowCommand.execute(this.buildTransferInput(
                fixture, new TransactionId(905L),
                Map.of("PAYER_FSP", fixture.payerOwnerId(), "PAYEE_FSP", fixture.payeeOwnerId()),
                Map.of("TRANSFER_AMOUNT", new BigDecimal("100.00")))));
    }

    @Test
    @DisplayName("Throw when required participant is missing")
    public void requiredParticipantNotFoundInTransaction() {

        final var fixture = this.createTransferPostingFixture(
            "POST_LEDGER_PARTICIPANT_MISSING", Currency.USD);

        assertThrows(
            RequiredParticipantNotFoundInTransactionException.class,
            () -> this.postAccountingFlowCommand.execute(this.buildTransferInput(
                fixture, new TransactionId(906L), Map.of("PAYER_FSP", fixture.payerOwnerId()),
                Map.of(
                    "TRANSFER_AMOUNT", new BigDecimal("100.00"), "PAYEE_FSP_FEE",
                    new BigDecimal("4.00")))));
    }

    @Test
    @DisplayName("Map restore failed error from ledger")
    public void restoreFailedInAccount() {

        final var fixture = this.createRestoreFailurePostingFixture(
            "POST_LEDGER_RESTORE", Currency.USD);
        final var transactionId = new TransactionId(911L);

        this.executeSql("DROP TRIGGER IF EXISTS trg_lgr_ledger_balance_fail_restore");
        this.executeSql("""
            CREATE TRIGGER trg_lgr_ledger_balance_fail_restore
            BEFORE UPDATE ON lgr_ledger_balance
            FOR EACH ROW
            BEGIN
                IF OLD.account_id = %d
                    AND NEW.posted_credits < OLD.posted_credits THEN
                    SIGNAL SQLSTATE '45000'
                    SET MESSAGE_TEXT = 'Forced ledger restore failure';
                END IF;
            END
            """.formatted(fixture.payeeAccountId().getId()));

        try {

            final var exception = assertThrows(
                RestoreFailedInAccountException.class,
                () -> this.postAccountingFlowCommand.execute(this.buildTransferInput(
                    fixture, transactionId, Map.of(
                        "PAYER_FSP", fixture.payerOwnerId(), "PAYEE_FSP",
                        fixture.payeeOwnerId()),
                    Map.of("TRANSFER_AMOUNT", new BigDecimal("100.00")))));

            assertEquals(
                this.code(fixture.prefix(), "ACC02"), exception.extras().get("accountCode"));

        } finally {

            this.executeSql("DROP TRIGGER IF EXISTS trg_lgr_ledger_balance_fail_restore");
        }
    }

    @Test
    @DisplayName("Post ledger flow successfully")
    public void successful() throws Exception {

        final var fixture = this.createTransferPostingFixture("POST_LEDGER_SUCCESS", Currency.USD);
        final var input = this.buildTransferInput(
            fixture, new TransactionId(901L),
            Map.of("PAYER_FSP", fixture.payerOwnerId(), "PAYEE_FSP", fixture.payeeOwnerId()),
            Map.of(
                "TRANSFER_AMOUNT", new BigDecimal("100.00"), "PAYEE_FSP_FEE",
                new BigDecimal("4.00")));

        final var output = this.postAccountingFlowCommand.execute(input);

        assertEquals(fixture.flowDefinitionId(), output.flowDefinitionId());
        assertEquals(2, output.movements().size());
        assertEquals(fixture.payerAccountId(), output.movements().getFirst().accountId());
        assertEquals(fixture.payeeAccountId(), output.movements().get(1).accountId());
        assertEquals(
            2L, this.queryForLong(
                "SELECT COUNT(*) FROM lgr_ledger_movement WHERE transaction_id = 901"));
    }

    @Test
    @DisplayName("Throw when flow definition is terminated")
    public void terminatedFlowDefinition() {

        final var fixture = this.createTransferPostingFixture(
            "POST_LEDGER_TERMINATED", Currency.USD);

        this.updateFlowDefinitionStatus(
            fixture.flowDefinitionId(), ActivationStatus.ACTIVE.name(),
            TerminationStatus.TERMINATED.name());

        assertThrows(
            FlowDefinitionNotConfiguredException.class,
            () -> this.postAccountingFlowCommand.execute(this.buildTransferInput(
                fixture, new TransactionId(904L),
                Map.of("PAYER_FSP", fixture.payerOwnerId(), "PAYEE_FSP", fixture.payeeOwnerId()),
                Map.of(
                    "TRANSFER_AMOUNT", new BigDecimal("100.00"), "PAYEE_FSP_FEE",
                    new BigDecimal("4.00")))));
    }

    private PostAccountingFlowCommand.Input buildTransferInput(final PostingFixture fixture,
                                                               final TransactionId transactionId,
                                                               final Map<String, AccountOwnerId> participants,
                                                               final Map<String, BigDecimal> amounts) {

        return new PostAccountingFlowCommand.Input(
            AccountingScenario.FUND_TRANSFER, Currency.USD,
            transactionId, Instant.parse("2026-01-01T00:00:00Z"), participants, amounts);
    }

    private String code(final String prefix, final String suffix) {

        final var compactPrefix = prefix.replace("_", "").replace("-", "");
        final var base =
            compactPrefix.length() > 12 ? compactPrefix.substring(0, 12) : compactPrefix;
        final var hash = Integer.toHexString(compactPrefix.hashCode()).toUpperCase();

        return base + "_" + hash + "_" + suffix;
    }

    private PostingFixture createRestoreFailurePostingFixture(final String prefix,
                                                              final Currency currency) {

        final var coaId = this.createCoa(this.createCoaCommand, prefix + "-coa");

        final var payerCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP",
            this.code(prefix, "PAYER01"), prefix + " Payer 01", AccountType.ASSET);

        final var payeeCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP",
            this.code(prefix, "PAYEE01"), prefix + " Payee 01", AccountType.REVENUE);

        final var payerOwnerId = new AccountOwnerId(Math.abs(prefix.hashCode()) + 1000L);
        final var payeeOwnerId = new AccountOwnerId(Math.abs(prefix.hashCode()) + 2000L);

        final var payerAccountId = this.createAccount(
            this.createAccountCommand, payerCoaEntryId,
            payerOwnerId.getId(), currency, this.code(prefix, "ACC01"), prefix + " Account 01");

        final var payeeAccountId = this.createAccount(
            this.createAccountCommand, payeeCoaEntryId,
            payeeOwnerId.getId(), currency, this.code(prefix, "ACC02"), prefix + " Account 02");

        final var flowDefinitionOutput = this.createFlowDefinitionCommand.execute(
            new CreateFlowDefinitionCommand.Input(
                AccountingScenario.FUND_TRANSFER, currency, prefix + "-flow",
                prefix + "-flow description", List.of(
                new CreateFlowDefinitionCommand.Input.FlowDefinitionLine(
                    1, "PAYEE_FSP",
                    payeeCoaEntryId, "TRANSFER_AMOUNT", Side.CREDIT, prefix + " payee line"),
                new CreateFlowDefinitionCommand.Input.FlowDefinitionLine(
                    2, "PAYER_FSP", payerCoaEntryId, "TRANSFER_AMOUNT", Side.CREDIT,
                    prefix + " payer line"))));

        return new PostingFixture(
            prefix, flowDefinitionOutput.flowDefinitionId(), payerAccountId, payeeAccountId,
            payerOwnerId, payeeOwnerId);
    }

    private PostingFixture createSingleAssetCreditPostingFixture(final String prefix,
                                                                 final Currency currency,
                                                                 final OverdraftMode overdraftMode,
                                                                 final BigDecimal overdraftLimit) {

        final var coaId = this.createCoa(this.createCoaCommand, prefix + "-coa");
        final var payerCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP",
            this.code(prefix, "PAYER01"), prefix + " Payer 01", AccountType.ASSET);
        final var payerOwnerId = new AccountOwnerId(Math.abs(prefix.hashCode()) + 1000L);
        final var payerAccountId = this.createAccount(
            this.createAccountCommand, payerCoaEntryId,
            payerOwnerId.getId(), currency, this.code(prefix, "ACC01"), prefix + " Account 01",
            overdraftMode, overdraftLimit);

        final var flowDefinitionOutput = this.createFlowDefinitionCommand.execute(
            new CreateFlowDefinitionCommand.Input(
                AccountingScenario.FUND_TRANSFER, currency, prefix + "-flow",
                prefix + "-flow description", List.of(
                new CreateFlowDefinitionCommand.Input.FlowDefinitionLine(
                    1, "PAYER_FSP", payerCoaEntryId, "TRANSFER_AMOUNT", Side.CREDIT,
                    prefix + " payer line"))));

        return new PostingFixture(
            prefix, flowDefinitionOutput.flowDefinitionId(), payerAccountId, null, payerOwnerId,
            null);
    }

    private PostingFixture createSingleAssetCreditPostingFixture(final String prefix,
                                                                 final Currency currency) {

        return this.createSingleAssetCreditPostingFixture(
            prefix, currency, OverdraftMode.FORBID, BigDecimal.ZERO);
    }

    private PostingFixture createSingleCreditPostingFixture(final String prefix,
                                                            final Currency currency) {

        final var coaId = this.createCoa(this.createCoaCommand, prefix + "-coa");
        final var payeeCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP",
            this.code(prefix, "PAYEE01"), prefix + " Payee 01", AccountType.REVENUE);
        final var payeeOwnerId = new AccountOwnerId(Math.abs(prefix.hashCode()) + 2000L);
        final var payeeAccountId = this.createAccount(
            this.createAccountCommand, payeeCoaEntryId,
            payeeOwnerId.getId(), currency, this.code(prefix, "ACC01"), prefix + " Account 01");

        final var flowDefinitionOutput = this.createFlowDefinitionCommand.execute(
            new CreateFlowDefinitionCommand.Input(
                AccountingScenario.FUND_TRANSFER, currency, prefix + "-flow",
                prefix + "-flow description", List.of(
                new CreateFlowDefinitionCommand.Input.FlowDefinitionLine(
                    1, "PAYEE_FSP", payeeCoaEntryId, "TRANSFER_AMOUNT", Side.CREDIT,
                    prefix + " payee line"))));

        return new PostingFixture(
            prefix, flowDefinitionOutput.flowDefinitionId(), null, payeeAccountId, null,
            payeeOwnerId);
    }

    private PostingFixture createTransferPostingFixture(final String prefix,
                                                        final Currency currency) {

        return this.createTransferPostingFixture(
            prefix, currency, OverdraftMode.FORBID, BigDecimal.ZERO);
    }

    private PostingFixture createTransferPostingFixture(final String prefix,
                                                        final Currency currency,
                                                        final OverdraftMode payerOverdraftMode,
                                                        final BigDecimal payerOverdraftLimit) {

        final var coaId = this.createCoa(this.createCoaCommand, prefix + "-coa");

        final var payerCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP",
            this.code(prefix, "PAYER01"), prefix + " Payer 01", AccountType.ASSET);

        final var payeeCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP",
            this.code(prefix, "PAYEE01"), prefix + " Payee 01", AccountType.REVENUE);

        final var payerOwnerId = new AccountOwnerId(Math.abs(prefix.hashCode()) + 1000L);

        final var payeeOwnerId = new AccountOwnerId(Math.abs(prefix.hashCode()) + 2000L);

        final var payerAccountId = this.createAccount(
            this.createAccountCommand, payerCoaEntryId, payerOwnerId.getId(), currency,
            this.code(prefix, "ACC01"), prefix + " Account 01", payerOverdraftMode,
            payerOverdraftLimit);

        final var payeeAccountId = this.createAccount(
            this.createAccountCommand, payeeCoaEntryId,
            payeeOwnerId.getId(), currency, this.code(prefix, "ACC02"), prefix + " Account 02");

        final var flowDefinitionOutput = this.createFlowDefinitionCommand.execute(
            new CreateFlowDefinitionCommand.Input(
                AccountingScenario.FUND_TRANSFER, currency, prefix + "-flow",
                prefix + "-flow description", List.of(
                new CreateFlowDefinitionCommand.Input.FlowDefinitionLine(
                    1, "PAYER_FSP", payerCoaEntryId,
                    FundTransferFlowDimension.Amounts.TRANSFER_AMOUNT.name(), Side.DEBIT,
                    prefix + " payer line"),
                new CreateFlowDefinitionCommand.Input.FlowDefinitionLine(
                    2, "PAYEE_FSP", payeeCoaEntryId,
                    FundTransferFlowDimension.Amounts.TRANSFER_AMOUNT.name(), Side.CREDIT,
                    prefix + " payee line"))));

        return new PostingFixture(
            prefix, flowDefinitionOutput.flowDefinitionId(), payerAccountId, payeeAccountId,
            payerOwnerId, payeeOwnerId);
    }

    private record PostingFixture(String prefix,
                                  FlowDefinitionId flowDefinitionId,
                                  AccountId payerAccountId,
                                  AccountId payeeAccountId,
                                  AccountOwnerId payerOwnerId,
                                  AccountOwnerId payeeOwnerId) { }

}
