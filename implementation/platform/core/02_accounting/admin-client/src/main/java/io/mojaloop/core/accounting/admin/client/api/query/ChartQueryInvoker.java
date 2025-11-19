package io.mojaloop.core.accounting.admin.client.api.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.accounting.admin.client.service.AccountingAdminService;
import io.mojaloop.core.accounting.contract.data.ChartData;
import io.mojaloop.core.accounting.contract.exception.chart.ChartIdNotFoundException;
import io.mojaloop.core.accounting.contract.query.ChartQuery;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChartQueryInvoker implements ChartQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChartQueryInvoker.class);

    private final AccountingAdminService.ChartQuery chartQuery;

    private final ObjectMapper objectMapper;

    public ChartQueryInvoker(final AccountingAdminService.ChartQuery chartQuery, final ObjectMapper objectMapper) {

        assert chartQuery != null;
        assert objectMapper != null;

        this.chartQuery = chartQuery;
        this.objectMapper = objectMapper;
    }

    @Override
    public ChartData get(final ChartId chartId) throws ChartIdNotFoundException {

        try {

            return RetrofitService.invoke(this.chartQuery.getByChartId(chartId),
                    (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ChartData> getAll() {

        try {

            return RetrofitService.invoke(this.chartQuery.getAllCharts(),
                    (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ChartData> getByNameContains(final String name) {

        try {

            return RetrofitService.invoke(this.chartQuery.getChartsByNameContains(name),
                    (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

}
