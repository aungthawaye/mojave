package io.mojaloop.core.accounting.admin.controller.chart;

import io.mojaloop.core.accounting.contract.data.ChartData;
import io.mojaloop.core.accounting.contract.exception.chart.ChartIdNotFoundException;
import io.mojaloop.core.accounting.contract.query.ChartQuery;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GetChartController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetChartController.class);

    private final ChartQuery chartQuery;

    public GetChartController(final ChartQuery chartQuery) {

        assert chartQuery != null;

        this.chartQuery = chartQuery;
    }

    @GetMapping("/charts/get-all-charts")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ChartData> allCharts() {

        return this.chartQuery.getAll();
    }

    @GetMapping("/charts/get-by-chart-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ChartData byChartId(@RequestParam final Long chartId) throws ChartIdNotFoundException {

        return this.chartQuery.get(new ChartId(chartId));
    }

    @GetMapping("/charts/get-by-name-contains")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ChartData> byNameContains(@RequestParam final String name) {

        return this.chartQuery.getByNameContains(name);
    }
}
