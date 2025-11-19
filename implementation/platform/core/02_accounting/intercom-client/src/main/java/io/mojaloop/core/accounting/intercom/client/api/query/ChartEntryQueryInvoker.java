package io.mojaloop.core.accounting.intercom.client.api.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.accounting.contract.data.ChartEntryData;
import io.mojaloop.core.accounting.contract.exception.chart.ChartEntryIdNotFoundException;
import io.mojaloop.core.accounting.contract.query.ChartEntryQuery;
import io.mojaloop.core.accounting.intercom.client.service.AccountingIntercomService;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChartEntryQueryInvoker implements ChartEntryQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChartEntryQueryInvoker.class);

    private final AccountingIntercomService.ChartQuery chartQuery;

    private final ObjectMapper objectMapper;

    public ChartEntryQueryInvoker(final AccountingIntercomService.ChartQuery chartQuery, final ObjectMapper objectMapper) {

        assert chartQuery != null;
        assert objectMapper != null;

        this.chartQuery = chartQuery;
        this.objectMapper = objectMapper;
    }

    @Override
    public ChartEntryData get(final ChartEntryId chartEntryId) throws ChartEntryIdNotFoundException {

        try {

            return RetrofitService.invoke(this.chartQuery.getByChartEntryId(chartEntryId),
                (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ChartEntryData> get(final ChartId chartId) {

        try {

            return RetrofitService.invoke(this.chartQuery.getEntriesByChartId(chartId),
                (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ChartEntryData> getAll() {

        try {

            return RetrofitService.invoke(this.chartQuery.getAllChartEntries(), (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper))
                                  .body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ChartEntryData> getByNameContains(final String name) {

        try {

            return RetrofitService.invoke(this.chartQuery.getChartEntriesByNameContains(name),
                (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

}
