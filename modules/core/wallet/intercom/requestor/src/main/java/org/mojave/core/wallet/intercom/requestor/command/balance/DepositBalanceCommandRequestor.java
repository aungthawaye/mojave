package org.mojave.core.wallet.intercom.requestor.command.balance;

import io.nats.client.Connection;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.CommandRequestor;
import org.mojave.core.wallet.contract.command.balance.DepositBalanceCommand;
import org.mojave.core.wallet.contract.exception.WalletExceptionResolver;
import org.mojave.core.wallet.contract.exception.balance.NoBalanceUpdateForTransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class DepositBalanceCommandRequestor implements DepositBalanceCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        DepositBalanceCommandRequestor.class);

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public DepositBalanceCommandRequestor(final Connection connection,
                                          final ObjectMapper objectMapper) {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.connection = connection;
        this.objectMapper = objectMapper;
    }

    @Override
    public Output execute(final Input input) throws NoBalanceUpdateForTransactionException {

        LOGGER.info("DepositBalanceCommandRequestor : input: ({})", ObjectLogger.log(input));

        try {

            final var output = CommandRequestor.request(
                this.connection,
                DepositBalanceCommand.SUBJECT_NAME, input, Output.class, this.objectMapper);

            LOGGER.info("DepositBalanceCommandRequestor : output : ({})", ObjectLogger.log(output));

            return output;

        } catch (final CommandRequestor.InvocationException exception) {

            final var decodedErrorResponse = exception.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof final MojaveErrorResponse errorResponse) {

                final var throwable = WalletExceptionResolver.resolve(errorResponse);

                if (throwable instanceof final NoBalanceUpdateForTransactionException noBalanceUpdateForTransactionException) {
                    throw noBalanceUpdateForTransactionException;
                }

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
