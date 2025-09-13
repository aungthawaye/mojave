package io.mojaloop.core.account.domain.command.chart;

import io.mojaloop.core.account.contract.command.chart.CreateChartCommand;
import io.mojaloop.core.account.contract.exception.chart.ChartNameRequiredException;
import io.mojaloop.core.account.domain.TestConfiguration;
import io.mojaloop.core.account.domain.repository.ChartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class CreateChartCommandIT {

    @Autowired
    private CreateChartCommand createChartCommand;

    @Autowired
    private ChartRepository chartRepository;

    @Test
    public void createChart_success_persistsAndReturnsId() {
        assertNotNull(createChartCommand);

        var out = createChartCommand.execute(new CreateChartCommand.Input("Default Chart"));
        assertNotNull(out);
        assertNotNull(out.chartId());
        var saved = chartRepository.findById(out.chartId());
        assertTrue(saved.isPresent());
        assertEquals("Default Chart", saved.get().getName());
        assertNotNull(saved.get().getCreatedAt());
    }

    @Test
    public void blankName_throwsChartNameRequiredException() {
        assertThrows(ChartNameRequiredException.class, () -> createChartCommand.execute(new CreateChartCommand.Input(" ")));
    }
}
