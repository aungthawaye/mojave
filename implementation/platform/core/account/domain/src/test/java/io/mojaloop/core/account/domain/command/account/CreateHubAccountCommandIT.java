package io.mojaloop.core.account.domain.command.account;

import io.mojaloop.core.account.contract.command.account.CreateHubAccountCommand;
import io.mojaloop.core.account.contract.command.chart.CreateChartCommand;
import io.mojaloop.core.account.contract.command.chart.CreateChartEntryCommand;
import io.mojaloop.core.account.domain.TestConfiguration;
import io.mojaloop.core.account.domain.repository.AccountRepository;
import io.mojaloop.core.account.domain.repository.ChartEntryRepository;
import io.mojaloop.core.account.domain.repository.ChartRepository;
import io.mojaloop.core.common.datatype.enums.account.AccountType;
import io.mojaloop.core.common.datatype.enums.account.OverdraftMode;
import io.mojaloop.core.common.datatype.enums.account.ChartType;
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
public class CreateHubAccountCommandIT {

    @Autowired
    private CreateChartCommand createChartCommand;

    @Autowired
    private CreateChartEntryCommand createChartEntryCommand;

    @Autowired
    private CreateHubAccountCommand createHubAccountCommand;

    @Autowired
    private ChartRepository chartRepository;

    @Autowired
    private ChartEntryRepository chartEntryRepository;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    public void cleanDatabase() {

        this.accountRepository.deleteAll();
        this.chartEntryRepository.deleteAll();
        this.chartRepository.deleteAll();
    }

    public void createAccount_fullFlow_success() throws Exception {

        var hubChart = this.createChartCommand.execute(new CreateChartCommand.Input("Hub CoA", ChartType.HUB));
        var fspChart = this.createChartCommand.execute(new CreateChartCommand.Input("FSP CoA", ChartType.HUB));
    }

    @Test
    public void createAccount_success_persistsAndReturnsId() throws Exception {

        var chartOut = this.createChartCommand.execute(new CreateChartCommand.Input("Main Chart", ChartType.HUB));
        var chartId = chartOut.chartId();

        assertTrue(this.chartRepository.findById(chartId).isPresent());

        var entryOut = this.createChartEntryCommand.execute(
            new CreateChartEntryCommand.Input(chartId, new ChartEntryCode("2000"), "Deposits", "Customer deposit accounts", AccountType.LIABILITY));
        var chartEntryId = entryOut.chartEntryId();

        assertTrue(this.chartEntryRepository.findById(chartEntryId).isPresent());

        var out = this.createHubAccountCommand.execute(
            new CreateHubAccountCommand.Input(chartEntryId, new HubId(12345L), Currency.USD, new AccountCode("DEPOSITS"), "Deposits Account",
                                              "Customer deposits", OverdraftMode.FORBID, BigDecimal.ZERO));

        assertNotNull(out);
        assertNotNull(out.accountId());

        var saved = this.accountRepository.findById(out.accountId());
        assertTrue(saved.isPresent());

        var acc = saved.get();

        assertEquals("Deposits Account", acc.getName());
        assertEquals("DEPOSITS", acc.getCode().value());
        assertEquals(Currency.USD, acc.getCurrency());
        assertEquals("Customer deposits", acc.getDescription());
        assertNotNull(acc.getCreatedAt());
        assertEquals(chartEntryId, acc.getChartEntryId());
        assertEquals(AccountType.LIABILITY, acc.getType());
    }

    @Test
    public void createAccount_withNonExistingChartEntry_throwsIllegalArgumentException() {
        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> this.createHubAccountCommand.execute(new CreateHubAccountCommand.Input(
            // Non-existing ChartEntryId
            new io.mojaloop.core.common.datatype.identifier.account.ChartEntryId(99999999L), new HubId(1L), Currency.USD, new AccountCode("CODE"), "Name",
            "Desc", OverdraftMode.FORBID, BigDecimal.ZERO)));
    }

}
