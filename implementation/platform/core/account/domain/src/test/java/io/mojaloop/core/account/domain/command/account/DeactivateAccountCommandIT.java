package io.mojaloop.core.account.domain.command.account;

import io.mojaloop.core.account.contract.command.account.CreateHubAccountCommand;
import io.mojaloop.core.account.contract.command.account.DeactivateAccountCommand;
import io.mojaloop.core.account.contract.command.chart.CreateChartCommand;
import io.mojaloop.core.account.contract.command.chart.CreateChartEntryCommand;
import io.mojaloop.core.account.contract.exception.account.AccountIdNotFoundException;
import io.mojaloop.core.account.domain.TestConfiguration;
import io.mojaloop.core.account.domain.repository.AccountRepository;
import io.mojaloop.core.account.domain.repository.ChartEntryRepository;
import io.mojaloop.core.account.domain.repository.ChartRepository;
import io.mojaloop.core.common.datatype.enums.ActivationStatus;
import io.mojaloop.core.common.datatype.enums.account.AccountType;
import io.mojaloop.core.common.datatype.enums.account.OverdraftMode;
import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import io.mojaloop.core.common.datatype.identifier.participant.HubId;
import io.mojaloop.core.common.datatype.type.account.AccountCode;
import io.mojaloop.core.common.datatype.type.account.ChartEntryCode;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class DeactivateAccountCommandIT {

    @Autowired
    private CreateChartCommand createChartCommand;

    @Autowired
    private CreateChartEntryCommand createChartEntryCommand;

    @Autowired
    private CreateHubAccountCommand createHubAccountCommand;

    @Autowired
    private DeactivateAccountCommand deactivateAccountCommand;

    @Autowired
    private ChartRepository chartRepository;

    @Autowired
    private ChartEntryRepository chartEntryRepository;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    public void cleanDatabase() {

        this.chartRepository.deleteAll();
        this.chartEntryRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

    @Test
    public void deactivateAccount_success_setsStatusInactive() throws Exception {

        // Arrange
        var chartOut = this.createChartCommand.execute(new CreateChartCommand.Input("Main Chart", io.mojaloop.core.common.datatype.enums.account.ChartType.HUB));
        var entryOut = this.createChartEntryCommand.execute(
            new CreateChartEntryCommand.Input(chartOut.chartId(), new ChartEntryCode("4000"), "Expense", "Expense accounts", AccountType.EXPENSE));

        var createOut = this.createHubAccountCommand.execute(
            new CreateHubAccountCommand.Input(entryOut.chartEntryId(), new HubId(5555L), Currency.USD, new AccountCode("EXP"), "Expense", "Expense acc",
                                              OverdraftMode.FORBID, BigDecimal.ZERO));

        // Act
        var out = this.deactivateAccountCommand.execute(new DeactivateAccountCommand.Input(createOut.accountId()));

        // Assert
        assertNotNull(out);
        var saved = this.accountRepository.findById(out.accountId());
        assertTrue(saved.isPresent());
        assertEquals(ActivationStatus.INACTIVE, saved.get().getActivationStatus());
    }

    @Test
    public void deactivateAccount_withNonExistingId_throwsAccountIdNotFoundException() {

        assertThrows(AccountIdNotFoundException.class, () -> this.deactivateAccountCommand.execute(new DeactivateAccountCommand.Input(new AccountId(222222222L))));
    }

}
