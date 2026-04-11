package org.mojave.core.accounting.domain.command.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.core.accounting.contract.command.account.CreateAccountCommand;
import org.mojave.core.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.core.accounting.contract.command.chart.CreateCoaEntryCommand;
import org.mojave.core.accounting.contract.exception.account.AccountCodeAlreadyExistsException;
import org.mojave.core.accounting.contract.exception.chart.CoaEntryIdNotFoundException;
import org.mojave.core.accounting.contract.query.AccountQuery;
import org.mojave.core.accounting.domain.AccountingDomainTestConfiguration;
import org.mojave.core.accounting.domain.BaseIT;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.accounting.AccountType;
import org.mojave.common.datatype.enums.accounting.OverdraftMode;
import org.mojave.common.datatype.identifier.accounting.AccountOwnerId;
import org.mojave.common.datatype.identifier.accounting.CoaEntryId;
import org.mojave.common.datatype.type.accounting.AccountCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        AccountingDomainTestConfiguration.class})
@DisplayName("Create Account Command Integration Test")
public class CreateAccountCommandIT extends BaseIT {

    @Autowired
    private CreateAccountCommand createAccountCommand;

    @Autowired
    private CreateCoaCommand createCoaCommand;

    @Autowired
    private CreateCoaEntryCommand createCoaEntryCommand;

    @Autowired
    private AccountQuery accountQuery;

    @Test
    @DisplayName("Throw when account code already exists")
    public void accountCodeAlreadyExists() {

        final var coaId = this.createCoa(this.createCoaCommand, "Duplicate Account CoA");

        final var firstCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP", "FSP_CASH_02", "FSP Cash 02",
            AccountType.ASSET);

        final var secondCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP", "FSP_CASH_03", "FSP Cash 03",
            AccountType.ASSET);

        this.createAccount(
            this.createAccountCommand, firstCoaEntryId, 201L, Currency.USD, "ACC_DUP_01",
            "Duplicate Account 01");

        assertThrows(
            AccountCodeAlreadyExistsException.class, () -> this.createAccount(
                this.createAccountCommand, secondCoaEntryId, 202L, Currency.USD, "ACC_DUP_01",
                "Duplicate Account 02"));
    }

    @Test
    @DisplayName("Throw when CoA entry id cannot be found")
    public void coaEntryIdNotFound() {

        assertThrows(
            CoaEntryIdNotFoundException.class,
            () -> this.createAccountCommand.execute(new CreateAccountCommand.Input(
                new CoaEntryId(Long.MAX_VALUE), new AccountOwnerId(102L), Currency.USD,
                new AccountCode("ACC_MISSING_ENTRY"), "Missing Entry Account",
                "Missing Entry Account description", OverdraftMode.FORBID, BigDecimal.ZERO)));
    }

    @Test
    @DisplayName("Rollback when ledger balance creation fails")
    public void ledgerBalanceCreationFailed() {

        final var coaId = this.createCoa(this.createCoaCommand, "LedgerOperation Failure CoA");
        final var coaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP", "LEDGER_FAIL_ENTRY_01",
            "LedgerOperation Fail Entry 01", AccountType.ASSET);

        this.executeSql("DROP TRIGGER IF EXISTS trg_lgr_ledger_balance_fail_insert");
        this.executeSql("""
            CREATE TRIGGER trg_lgr_ledger_balance_fail_insert
            BEFORE INSERT ON lgr_ledger_balance
            FOR EACH ROW
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Forced ledger balance insert failure'
            """);

        try {

            assertThrows(
                RuntimeException.class, () -> this.createAccount(
                    this.createAccountCommand, coaEntryId, 203L, Currency.USD,
                    "ACC_LEDGER_FAIL_01", "LedgerOperation Failure Account 01"));
            assertEquals(0, this.accountQuery.getAll().size());

        } finally {

            this.executeSql("DROP TRIGGER IF EXISTS trg_lgr_ledger_balance_fail_insert");
        }
    }

    @Test
    @DisplayName("Create account successfully")
    public void successful() {

        final var coaId = this.createCoa(this.createCoaCommand, "Account CoA");
        final var coaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP", "FSP_CASH_01", "FSP Cash 01",
            AccountType.ASSET);
        final var output = this.createAccountCommand.execute(new CreateAccountCommand.Input(
            coaEntryId, new AccountOwnerId(101L), Currency.USD, new AccountCode("ACC_CASH_01"),
            "FSP Cash Account", "FSP Cash Account description", OverdraftMode.FORBID,
            BigDecimal.ZERO));
        final var account = this.accountQuery.get(output.accountId());

        assertNotNull(output.accountId());
        assertEquals(Currency.USD, account.currency());
        assertEquals("ACC_CASH_01", account.code().value());
        assertEquals(coaEntryId, account.coaEntryId());
    }

}
