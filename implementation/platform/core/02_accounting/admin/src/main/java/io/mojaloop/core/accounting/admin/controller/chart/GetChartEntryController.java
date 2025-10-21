package io.mojaloop.core.accounting.admin.controller.chart;

import io.mojaloop.core.accounting.contract.data.ChartEntryData;
import io.mojaloop.core.accounting.contract.exception.chart.ChartEntryIdNotFoundException;
import io.mojaloop.core.accounting.contract.query.ChartEntryQuery;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
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
public class GetChartEntryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetChartEntryController.class);

    private final ChartEntryQuery chartEntryQuery;

    public GetChartEntryController(final ChartEntryQuery chartEntryQuery) {

        assert chartEntryQuery != null;

        this.chartEntryQuery = chartEntryQuery;
    }

    @GetMapping("/charts/entries/get-all-entries")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ChartEntryData> allEntries() {

        return this.chartEntryQuery.getAll();
    }

    @GetMapping("/charts/entries/get-by-chart-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ChartEntryData> byChartId(@RequestParam final Long chartId) {

        return this.chartEntryQuery.get(new ChartId(chartId));
    }

    @GetMapping("/charts/entries/get-by-entry-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ChartEntryData byEntryId(@RequestParam final Long entryId) throws ChartEntryIdNotFoundException {

        return this.chartEntryQuery.get(new ChartEntryId(entryId));
    }

    @GetMapping("/charts/entries/get-by-name-contains")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ChartEntryData> byNameContains(@RequestParam final String name) {

        return this.chartEntryQuery.getByNameContains(name);
    }

}
