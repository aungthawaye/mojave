package io.mojaloop.core.wallet.intercom.client.api.command.position;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.misc.exception.UncheckedDomainException;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.wallet.contract.command.position.FulfilPositionsCommand;
import io.mojaloop.core.wallet.contract.exception.WalletExceptionResolver;
import io.mojaloop.core.wallet.contract.exception.position.FailedToCommitReservationException;
import io.mojaloop.core.wallet.intercom.client.service.WalletIntercomService;
import org.springframework.stereotype.Component;

@Component
public class FulfilPositionsInvoker implements FulfilPositionsCommand {

    private final WalletIntercomService.PositionCommand positionCommand;

    private final ObjectMapper objectMapper;

    public FulfilPositionsInvoker(final WalletIntercomService.PositionCommand positionCommand,
                                  final ObjectMapper objectMapper) {

        assert positionCommand != null;
        assert objectMapper != null;

        this.positionCommand = positionCommand;
        this.objectMapper = objectMapper;
    }

    @Override
    public Output execute(final Input input) throws FailedToCommitReservationException {

        try {
            return RetrofitService.invoke(
                this.positionCommand.fulfil(input),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (final RetrofitService.InvocationException e) {

            final var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse errorResponse) {

                final var throwable = WalletExceptionResolver.resolve(errorResponse);

                switch (throwable) {
                    case FailedToCommitReservationException ex -> throw ex;
                    case UncheckedDomainException ude -> throw ude;
                    default -> {
                    }
                }
            }

            throw new RuntimeException(e);
        }
    }

}
