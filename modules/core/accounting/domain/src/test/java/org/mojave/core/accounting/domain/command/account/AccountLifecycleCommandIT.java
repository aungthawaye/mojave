package org.mojave.core.accounting.domain.command.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.TerminationStatus;
import org.mojave.common.datatype.enums.accounting.AccountType;
import org.mojave.common.datatype.enums.accounting.ChartEntryCategory;
import org.mojave.common.datatype.identifier.accounting.AccountId;
import org.mojave.common.datatype.identifier.accounting.AccountOwnerId;
import org.mojave.common.datatype.type.participant.FspCode;
import org.mojave.core.accounting.contract.command.account.ChangeAccountPropertiesCommand;
import org.mojave.core.accounting.contract.command.account.CreateAccountByCategoryCommand;
import org.mojave.core.accounting.contract.command.account.CreateAccountCommand;
import org.mojave.core.accounting.contract.command.account.DeactivateAccountCommand;
import org.mojave.core.accounting.contract.command.account.TerminateAccountCommand;
import org.mojave.core.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.core.accounting.contract.command.chart.CreateCoaEntryCommand;
import org.mojave.core.accounting.contract.exception.account.AccountIdNotFoundException;
import org.mojave.core.accounting.contract.query.AccountQuery;
import org.mojave.core.accounting.domain.AccountingDomainConfiguration;
import org.mojave.core.accounting.domain.AccountingDomainSettings;
import org.mojave.core.accounting.domain.BaseIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Comparator;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        AccountingDomainConfiguration.class,
        AccountingDomainSettings.class})
@DisplayName("Account Lifecycle Commands Integration Test")
public class AccountLifecycleCommandIT extends BaseIT {

    @Autowired
    private ChangeAccountPropertiesCommand changeAccountPropertiesCommand;

    @Autowired
    private CreateAccountByCategoryCommand createAccountByCategoryCommand;

    @Autowired
    private CreateAccountCommand createAccountCommand;

    @Autowired
    private CreateCoaCommand createCoaCommand;

    @Autowired
    private CreateCoaEntryCommand createCoaEntryCommand;

    @Autowired
    private DeactivateAccountCommand deactivateAccountCommand;

    @Autowired
    private TerminateAccountCommand terminateAccountCommand;

    @Autowired
    private AccountQuery accountQuery;

    @Test
    @DisplayName("Change account properties successfully")
    public void changeAccountPropertiesSuccessful() {

        final var coaId = this.createCoa(this.createCoaCommand, "Change Account CoA");
        final var coaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand,
            coaId,
            ChartEntryCategory.FSP,
            "CHANGE_ACC_ENTRY_01",
            "Change Account Entry 01",
            AccountType.ASSET);
        final var accountId = this.createAccount(
            this.createAccountCommand,
            coaEntryId,
            601L,
            Currency.USD,
            "CHANGE_ACC_01",
            "Change Account 01");

        final var output = this.changeAccountPropertiesCommand.execute(
            new ChangeAccountPropertiesCommand.Input(
                accountId,
                "Changed Account Name",
                "Changed Account Description"));
        final var account = this.accountQuery.get(accountId);

        assertEquals(accountId, output.accountId());
        assertEquals("Changed Account Name", account.name());
        assertEquals("Changed Account Description", account.description());
    }

    @Test
    @DisplayName("Keep account properties unchanged when optional fields are null")
    public void changeAccountPropertiesWithNullValues() {

        final var coaId = this.createCoa(this.createCoaCommand, "Change Account Null CoA");
        final var coaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand,
            coaId,
            ChartEntryCategory.FSP,
            "CHANGE_ACC_ENTRY_02",
            "Change Account Entry 02",
            AccountType.ASSET);
        final var accountId = this.createAccount(
            this.createAccountCommand,
            coaEntryId,
            602L,
            Currency.USD,
            "CHANGE_ACC_02",
            "Change Account 02");

        this.changeAccountPropertiesCommand.execute(
            new ChangeAccountPropertiesCommand.Input(accountId, null, null));

        final var account = this.accountQuery.get(accountId);

        assertEquals("Change Account 02", account.name());
        assertEquals("Change Account 02 description", account.description());
    }

    @Test
    @DisplayName("Throw when changing properties for missing account")
    public void changeAccountPropertiesAccountIdNotFound() {

        assertThrows(
            AccountIdNotFoundException.class,
            () -> this.changeAccountPropertiesCommand.execute(
                new ChangeAccountPropertiesCommand.Input(
                    new AccountId(Long.MAX_VALUE),
                    "Missing",
                    "Missing")));
    }

    @Test
    @DisplayName("Create accounts by category successfully")
    public void createAccountByCategorySuccessful() {

        final var coaId = this.createCoa(this.createCoaCommand, "Create By Category CoA");
        this.createCoaEntry(
            this.createCoaEntryCommand,
            coaId,
            ChartEntryCategory.FSP,
            "CREATE_BY_CATEGORY_01",
            "Create By Category 01",
            AccountType.ASSET);
        this.createCoaEntry(
            this.createCoaEntryCommand,
            coaId,
            ChartEntryCategory.FSP,
            "CREATE_BY_CATEGORY_02",
            "Create By Category 02",
            AccountType.LIABILITY);
        this.createCoaEntry(
            this.createCoaEntryCommand,
            coaId,
            ChartEntryCategory.HUB,
            "CREATE_BY_CATEGORY_03",
            "Create By Category 03",
            AccountType.REVENUE);

        final var output = this.createAccountByCategoryCommand.execute(
            new CreateAccountByCategoryCommand.Input(
                new AccountOwnerId(603L),
                new FspCode("FSP01"),
                Currency.USD,
                ChartEntryCategory.FSP));
        final var accounts = output.accountIds()
                                   .stream()
                                   .map(this.accountQuery::get)
                                   .sorted(Comparator.comparing(account -> account.code().value()))
                                   .toList();

        assertEquals(2, output.accountIds().size());
        assertEquals(
            "FSP01-CREATE_BY_CATEGORY_01-USD",
            accounts.getFirst().code().value());
        assertEquals(
            "FSP01-CREATE_BY_CATEGORY_02-USD",
            accounts.get(1).code().value());
    }

    @Test
    @DisplayName("Return empty output when no entry matches category")
    public void createAccountByCategoryWithoutMatches() {

        final var coaId = this.createCoa(this.createCoaCommand, "Create By Category Empty CoA");
        this.createCoaEntry(
            this.createCoaEntryCommand,
            coaId,
            ChartEntryCategory.HUB,
            "CREATE_BY_CATEGORY_EMPTY_01",
            "Create By Category Empty 01",
            AccountType.ASSET);

        final var output = this.createAccountByCategoryCommand.execute(
            new CreateAccountByCategoryCommand.Input(
                new AccountOwnerId(604L),
                new FspCode("FSP02"),
                Currency.USD,
                ChartEntryCategory.FSP));

        assertEquals(0, output.accountIds().size());
        assertEquals(
            0,
            this.accountQuery.getAll()
                .stream()
                .map(account -> account.accountId().getId())
                .collect(Collectors.toSet())
                .size());
    }

    @Test
    @DisplayName("Deactivate account successfully")
    public void deactivateAccountSuccessful() {

        final var coaId = this.createCoa(this.createCoaCommand, "Deactivate Account CoA");
        final var coaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand,
            coaId,
            ChartEntryCategory.FSP,
            "DEACTIVATE_ACC_ENTRY_01",
            "Deactivate Account Entry 01",
            AccountType.ASSET);
        final var accountId = this.createAccount(
            this.createAccountCommand,
            coaEntryId,
            605L,
            Currency.USD,
            "DEACTIVATE_ACC_01",
            "Deactivate Account 01");

        final var output = this.deactivateAccountCommand.execute(
            new DeactivateAccountCommand.Input(accountId));
        final var account = this.accountQuery.get(accountId);

        assertEquals(accountId, output.accountId());
        assertEquals(ActivationStatus.INACTIVE, account.activationStatus());
    }

    @Test
    @DisplayName("Throw when deactivating missing account")
    public void deactivateAccountNotFound() {

        assertThrows(
            AccountIdNotFoundException.class,
            () -> this.deactivateAccountCommand.execute(
                new DeactivateAccountCommand.Input(new AccountId(Long.MAX_VALUE))));
    }

    @Test
    @DisplayName("Terminate account successfully")
    public void terminateAccountSuccessful() {

        final var coaId = this.createCoa(this.createCoaCommand, "Terminate Account CoA");
        final var coaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand,
            coaId,
            ChartEntryCategory.FSP,
            "TERMINATE_ACC_ENTRY_01",
            "Terminate Account Entry 01",
            AccountType.ASSET);
        final var accountId = this.createAccount(
            this.createAccountCommand,
            coaEntryId,
            606L,
            Currency.USD,
            "TERMINATE_ACC_01",
            "Terminate Account 01");

        final var output = this.terminateAccountCommand.execute(
            new TerminateAccountCommand.Input(accountId));
        final var account = this.accountQuery.get(accountId);

        assertEquals(accountId, output.accountId());
        assertEquals(TerminationStatus.TERMINATED, account.terminationStatus());
        assertEquals(ActivationStatus.ACTIVE, account.activationStatus());
    }

    @Test
    @DisplayName("Throw when terminating missing account")
    public void terminateAccountNotFound() {

        assertThrows(
            AccountIdNotFoundException.class,
            () -> this.terminateAccountCommand.execute(
                new TerminateAccountCommand.Input(new AccountId(Long.MAX_VALUE))));
    }

}
