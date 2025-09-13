package io.mojaloop.core.account.domain.command.chart;

import io.mojaloop.core.account.contract.command.chart.CreateChartCommand;
import io.mojaloop.core.account.contract.command.chart.CreateChartEntryCommand;
import io.mojaloop.core.account.contract.exception.chart.ChartIdNotFoundException;
import io.mojaloop.core.account.domain.TestConfiguration;
import io.mojaloop.core.account.domain.repository.ChartEntryRepository;
import io.mojaloop.core.account.domain.repository.ChartRepository;
import io.mojaloop.core.common.datatype.enums.account.AccountType;
import io.mojaloop.core.common.datatype.type.account.ChartEntryCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class CreateChartEntryCommandIT {

    @Autowired
    private CreateChartCommand createChartCommand;

    @Autowired
    private CreateChartEntryCommand createChartEntryCommand;

    @Autowired
    private ChartRepository chartRepository;

    @Autowired
    private ChartEntryRepository chartEntryRepository;

    @Test
    public void createChartEntry_success_persistsAndReturnsId() throws ChartIdNotFoundException {
        // Arrange: create a chart first
        var chartOut = createChartCommand.execute(new CreateChartCommand.Input("Main Chart"));
        var chartId = chartOut.chartId();
        assertTrue(chartRepository.findById(chartId).isPresent());

        // Act
        var entryOut = createChartEntryCommand.execute(
                new CreateChartEntryCommand.Input(chartId, new ChartEntryCode("1000"), "Cash",
                        "Cash and cash equivalents", AccountType.ASSET));

        // Assert
        assertNotNull(entryOut);
        assertNotNull(entryOut.chartEntryId());
        var saved = chartEntryRepository.findById(entryOut.chartEntryId());
        assertTrue(saved.isPresent());
        assertEquals("Cash", saved.get().getName());
        assertEquals("1000", saved.get().getCode().value());
        assertEquals(AccountType.ASSET, saved.get().getAccountType());
        assertNotNull(saved.get().getCreatedAt());
        assertEquals(chartId, saved.get().getChart().getId());
    }
}
