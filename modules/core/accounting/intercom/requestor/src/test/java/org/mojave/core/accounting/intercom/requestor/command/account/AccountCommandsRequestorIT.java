package org.mojave.core.accounting.intercom.requestor.command.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.core.accounting.contract.command.account.ActivateAccountCommand;
import org.mojave.core.accounting.contract.command.account.ChangeAccountPropertiesCommand;
import org.mojave.core.accounting.contract.command.account.CreateAccountByCategoryCommand;
import org.mojave.core.accounting.contract.command.account.CreateAccountCommand;
import org.mojave.core.accounting.contract.command.account.DeactivateAccountCommand;
import org.mojave.core.accounting.contract.command.account.TerminateAccountCommand;
import org.mojave.core.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.core.accounting.contract.command.chart.CreateCoaEntryCommand;
import org.mojave.core.accounting.intercom.requestor.AccountingIntercomRequestorTestConfiguration;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.accounting.AccountType;
import org.mojave.scheme.rule.enums.accounting.OverdraftMode;
import org.mojave.scheme.rule.identifier.accounting.AccountOwnerId;
import org.mojave.scheme.rule.type.accounting.AccountCode;
import org.mojave.scheme.rule.type.accounting.CoaEntryCode;
import org.mojave.scheme.rule.type.participant.FspCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        AccountingIntercomRequestorTestConfiguration.class})
@DisplayName("Account Commands Requestor Integration Test")
public class AccountCommandsRequestorIT {

    @Autowired
    private ActivateAccountCommand activateAccountCommand;

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

    @Test
    @DisplayName("Execute account commands through requestor")
    public void successful() {

        final var suffix = Long.toString(System.currentTimeMillis());

        final var createCoaOutput = this.createCoaCommand.execute(
            new CreateCoaCommand.Input("requestor-account-coa-" + suffix));

        final var firstCoaEntryOutput = this.createCoaEntryCommand.execute(
            new CreateCoaEntryCommand.Input(
                createCoaOutput.coaId(),
                "FSP",
                new CoaEntryCode("REQ_ACC_01_" + suffix),
                "Requestor Account Entry 01 " + suffix,
                "Requestor Account Entry 01 Description " + suffix,
                AccountType.ASSET));

        this.createCoaEntryCommand.execute(
            new CreateCoaEntryCommand.Input(
                createCoaOutput.coaId(),
                "FSP",
                new CoaEntryCode("REQ_ACC_02_" + suffix),
                "Requestor Account Entry 02 " + suffix,
                "Requestor Account Entry 02 Description " + suffix,
                AccountType.LIABILITY));

        final var createAccountOutput = this.createAccountCommand.execute(
            new CreateAccountCommand.Input(
                firstCoaEntryOutput.coaEntryId(),
                new AccountOwnerId(700_001L),
                Currency.USD,
                new AccountCode("REQ_ACC_MAIN_" + suffix),
                "Requestor Account " + suffix,
                "Requestor Account Description " + suffix,
                OverdraftMode.FORBID,
                BigDecimal.ZERO));

        final var changeAccountOutput = this.changeAccountPropertiesCommand.execute(
            new ChangeAccountPropertiesCommand.Input(
                createAccountOutput.accountId(),
                "Requestor Account Updated " + suffix,
                "Requestor Account Description Updated " + suffix));

        final var deactivateOutput = this.deactivateAccountCommand.execute(
            new DeactivateAccountCommand.Input(createAccountOutput.accountId()));

        final var activateOutput = this.activateAccountCommand.execute(
            new ActivateAccountCommand.Input(createAccountOutput.accountId()));

        final var createByCategoryOutput = this.createAccountByCategoryCommand.execute(
            new CreateAccountByCategoryCommand.Input(
                new AccountOwnerId(700_002L),
                new FspCode("FSP" + suffix),
                Currency.USD,
                "FSP"));

        final var terminateOutput = this.terminateAccountCommand.execute(
            new TerminateAccountCommand.Input(createAccountOutput.accountId()));

        assertNotNull(createAccountOutput.accountId());

        assertEquals(createAccountOutput.accountId(), changeAccountOutput.accountId());
        assertEquals(createAccountOutput.accountId(), deactivateOutput.accountId());
        assertEquals(createAccountOutput.accountId(), activateOutput.accountId());
        assertEquals(createAccountOutput.accountId(), terminateOutput.accountId());

        assertNotNull(createByCategoryOutput.accountIds());
    }

}
