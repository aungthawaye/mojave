package io.mojaloop.core.accounting.admin.client.api.command.definition;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.misc.exception.UncheckedDomainException;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.accounting.admin.client.service.AccountingAdminService;
import io.mojaloop.core.accounting.contract.command.definition.ChangeFlowDefinitionCurrencyCommand;
import io.mojaloop.core.accounting.contract.exception.AccountingExceptionResolver;

public class ChangeFlowDefinitionCurrencyInvoker implements ChangeFlowDefinitionCurrencyCommand {

    private final AccountingAdminService.DefinitionCommand definitionCommand;

    private final ObjectMapper objectMapper;

    public ChangeFlowDefinitionCurrencyInvoker(final AccountingAdminService.DefinitionCommand definitionCommand, final ObjectMapper objectMapper) {

        assert definitionCommand != null;
        assert objectMapper != null;

        this.definitionCommand = definitionCommand;
        this.objectMapper = objectMapper;
    }

    @Override
    public Output execute(final Input input) {

        try {

            return RetrofitService.invoke(this.definitionCommand.changeCurrency(input), (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper))
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
