package io.mojaloop.core.account.domain.command.chart;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.core.account.contract.command.chart.CreateChartCommand;
import io.mojaloop.core.account.domain.model.Chart;
import io.mojaloop.core.account.domain.repository.ChartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateChartCommandHandler implements CreateChartCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateChartCommandHandler.class);

    private final ChartRepository chartRepository;

    public CreateChartCommandHandler(ChartRepository chartRepository) {

        assert chartRepository != null;
        this.chartRepository = chartRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("Executing CreateChartCommand with input: {}", input);

        var chart = new Chart(input.name(), input.type());
        LOGGER.info("Created Chart: {}", chart);

        chart = this.chartRepository.save(chart);
        LOGGER.info("Saved Chart with id: {}", chart.getId());

        LOGGER.info("Completed CreateChartCommand with input: {}", input);

        return new Output(chart.getId());
    }

}
