package org.mojave.wallet.intercom.requestor.command.balance;

import io.nats.client.Connection;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.CommandRequestor;
import org.mojave.wallet.contract.command.balance.ReverseBalanceWithdrawCommand;
import org.mojave.wallet.contract.exception.WalletExceptionResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class ReverseBalanceWithdrawRequestor implements ReverseBalanceWithdrawCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReverseBalanceWithdrawRequestor.class);

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public ReverseBalanceWithdrawRequestor(final Connection connection,
                final ObjectMapper objectMapper) {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.connection = connection;
        this.objectMapper = objectMapper;
    }

    @Override
    public Output execute(final Input input) {

        LOGGER.info("ReverseBalanceWithdrawRequestor : input: ({})", ObjectLogger.log(input));

        try {

            final var output = CommandRequestor.request(
                this.connection, ReverseBalanceWithdrawCommand.SUBJECT_NAME,
                input, Output.class, this.objectMapper);

            LOGGER.info("ReverseBalanceWithdrawRequestor : output : ({})", ObjectLogger.log(output));

            return output;

        } catch (final CommandRequestor.InvocationException exception) {

            final var decodedErrorResponse = exception.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof final MojaveErrorResponse errorResponse) {

                final var throwable = WalletExceptionResolver.resolve(errorResponse);

                if (throwable instanceof final UncheckedDomainException uncheckedDomainException) {
                    throw uncheckedDomainException;
                }

                if (throwable instanceof final RuntimeException runtimeException) {
                    throw runtimeException;
                }
            }

            throw new RuntimeException(exception);
        }
    }

}
