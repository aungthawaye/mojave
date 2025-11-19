package io.mojaloop.core.accounting.admin.client.api.command.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.misc.exception.UncheckedDomainException;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.accounting.admin.client.service.AccountingAdminService;
import io.mojaloop.core.accounting.contract.command.account.DeactivateAccountCommand;
import io.mojaloop.core.accounting.contract.exception.AccountingExceptionResolver;

public class DeactivateAccountInvoker implements DeactivateAccountCommand {

    private final AccountingAdminService.AccountCommand accountCommand;

    private final ObjectMapper objectMapper;

    public DeactivateAccountInvoker(final AccountingAdminService.AccountCommand accountCommand, final ObjectMapper objectMapper) {

        assert accountCommand != null;
        assert objectMapper != null;

        this.accountCommand = accountCommand;
        this.objectMapper = objectMapper;
    }

    @Override
    public Output execute(final Input input) {

        try {

            return RetrofitService.invoke(this.accountCommand.deactivate(input), (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper))
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
