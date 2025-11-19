package io.mojaloop.core.accounting.admin.client.api.command.chart;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.misc.exception.UncheckedDomainException;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.accounting.admin.client.service.AccountingAdminService;
import io.mojaloop.core.accounting.contract.command.chart.ChangeChartNameCommand;
import io.mojaloop.core.accounting.contract.exception.AccountingExceptionResolver;

public class ChangeChartNameInvoker implements ChangeChartNameCommand {

    private final AccountingAdminService.ChartCommand chartCommand;

    private final ObjectMapper objectMapper;

    public ChangeChartNameInvoker(final AccountingAdminService.ChartCommand chartCommand, final ObjectMapper objectMapper) {

        assert chartCommand != null;
        assert objectMapper != null;

        this.chartCommand = chartCommand;
        this.objectMapper = objectMapper;
    }

    @Override
    public Output execute(final Input input) {

        try {

            return RetrofitService.invoke(this.chartCommand.changeName(input), (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper))
                                  .body();

        } catch (RetrofitService.InvocationException e) {

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse errorResponse) {

                var throwable = AccountingExceptionResolver.resolve(errorResponse);

                if (throwable instanceof UncheckedDomainException ude) {
                    throw ude;
                }
            }

            throw new RuntimeException(e);
        }
    }
}
