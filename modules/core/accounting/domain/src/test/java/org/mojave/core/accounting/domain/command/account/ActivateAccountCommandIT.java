package org.mojave.core.accounting.domain.command.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.accounting.AccountType;
import org.mojave.common.datatype.enums.accounting.ChartEntryCategory;
import org.mojave.common.datatype.identifier.accounting.AccountId;
import org.mojave.core.accounting.contract.command.account.ActivateAccountCommand;
import org.mojave.core.accounting.contract.command.account.DeactivateAccountCommand;
import org.mojave.core.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.core.accounting.contract.command.chart.CreateCoaEntryCommand;
import org.mojave.core.accounting.contract.command.account.CreateAccountCommand;
import org.mojave.core.accounting.contract.exception.account.AccountIdNotFoundException;
import org.mojave.core.accounting.contract.query.AccountQuery;
import org.mojave.core.accounting.domain.AccountingDomainConfiguration;
import org.mojave.core.accounting.domain.AccountingDomainSettings;
import org.mojave.core.accounting.domain.BaseIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        AccountingDomainConfiguration.class,
        AccountingDomainSettings.class})
@DisplayName("Activate Account Command Integration Test")
public class ActivateAccountCommandIT extends BaseIT {

    @Autowired
    private ActivateAccountCommand activateAccountCommand;

    @Autowired
    private CreateAccountCommand createAccountCommand;

    @Autowired
    private CreateCoaCommand createCoaCommand;

    @Autowired
    private CreateCoaEntryCommand createCoaEntryCommand;

    @Autowired
    private DeactivateAccountCommand deactivateAccountCommand;

    @Autowired
    private AccountQuery accountQuery;

    @Test
    @DisplayName("Activate account successfully")
    public void successful() {

        final var coaId = this.createCoa(this.createCoaCommand, "Activate Account CoA");
        final var coaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand,
            coaId,
            ChartEntryCategory.FSP,
            "ACTIVATE_ACC_ENTRY_01",
            "Activate Account Entry 01",
            AccountType.ASSET);
        final var accountId = this.createAccount(
            this.createAccountCommand,
            coaEntryId,
            501L,
            Currency.USD,
            "ACTIVATE_ACC_01",
            "Activate Account 01");

        this.deactivateAccountCommand.execute(new DeactivateAccountCommand.Input(accountId));

        final var output = this.activateAccountCommand.execute(
            new ActivateAccountCommand.Input(accountId));
        final var account = this.accountQuery.get(accountId);

        assertEquals(accountId, output.accountId());
        assertEquals(ActivationStatus.ACTIVE, account.activationStatus());
    }

    @Test
    @DisplayName("Throw when account id cannot be found")
    public void accountIdNotFound() {

        assertThrows(
            AccountIdNotFoundException.class,
            () -> this.activateAccountCommand.execute(
                new ActivateAccountCommand.Input(new AccountId(Long.MAX_VALUE))));
    }

}
