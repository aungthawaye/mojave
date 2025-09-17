package io.mojaloop.core.account.domain.command.account;

import io.mojaloop.core.account.contract.command.account.ChangeAccountPropertiesCommand;
import io.mojaloop.core.account.contract.command.account.CreateHubAccountCommand;
import io.mojaloop.core.account.contract.command.chart.CreateChartCommand;
import io.mojaloop.core.account.contract.command.chart.CreateChartEntryCommand;
import io.mojaloop.core.account.contract.exception.account.AccountIdNotFoundException;
import io.mojaloop.core.account.domain.TestConfiguration;
import io.mojaloop.core.account.domain.repository.AccountRepository;
import io.mojaloop.core.account.domain.repository.ChartEntryRepository;
import io.mojaloop.core.account.domain.repository.ChartRepository;
import io.mojaloop.core.common.datatype.enums.account.AccountType;
import io.mojaloop.core.common.datatype.enums.account.ChartType;
import io.mojaloop.core.common.datatype.enums.account.OverdraftMode;
import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import io.mojaloop.core.common.datatype.identifier.participant.HubId;
import io.mojaloop.core.common.datatype.type.account.AccountCode;
import io.mojaloop.core.common.datatype.type.account.ChartEntryCode;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class ChangeAccountPropertiesCommandIT {

    @Autowired
    private CreateChartCommand createChartCommand;

    @Autowired
    private CreateChartEntryCommand createChartEntryCommand;

    @Autowired
    private CreateHubAccountCommand createHubAccountCommand;

    @Autowired
    private ChangeAccountPropertiesCommand changeAccountPropertiesCommand;

    @Autowired
    private ChartRepository chartRepository;

    @Autowired
    private ChartEntryRepository chartEntryRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void changeProperties_success_updatesMutableFields() throws Exception {
        // Arrange
        var chartOut = this.createChartCommand.execute(new CreateChartCommand.Input("Main Chart", ChartType.HUB));
        var entryOut = this.createChartEntryCommand.execute(
            new CreateChartEntryCommand.Input(chartOut.chartId(), new ChartEntryCode("6000"), "Assets",
                                              "Asset accounts", AccountType.ASSET));

        var createOut = this.createHubAccountCommand.execute(new CreateHubAccountCommand.Input(
            entryOut.chartEntryId(), new HubId(9999L), Currency.USD,
            new AccountCode("AST"), "Assets", "Assets acc", OverdraftMode.FORBID, BigDecimal.ZERO));

        // Act
        var out = this.changeAccountPropertiesCommand.execute(new ChangeAccountPropertiesCommand.Input(
            createOut.accountId(), new AccountCode("AST2"), "Assets Updated", "Updated description"));

        // Assert
        assertNotNull(out);
        var saved = this.accountRepository.findById(out.accountId());
        assertTrue(saved.isPresent());
        var acc = saved.get();
        assertEquals("Assets Updated", acc.getName());
        assertEquals("AST2", acc.getCode().value());
        assertEquals("Updated description", acc.getDescription());
    }

    @Test
    public void changeProperties_withNonExistingId_throwsAccountIdNotFoundException() {

        assertThrows(AccountIdNotFoundException.class, () ->
                                                           this.changeAccountPropertiesCommand.execute(new ChangeAccountPropertiesCommand.Input(
                                                               new AccountId(444444444L), new AccountCode("X"), "N", "D"))
                    );
    }

}
