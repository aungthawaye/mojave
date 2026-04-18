package org.mojave.core.wallet.intercom.requestor.command.position;

import io.nats.client.Connection;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.NatsRequestor;
import org.mojave.core.wallet.contract.command.position.ReservePositionCommand;
import org.mojave.core.wallet.contract.exception.WalletNotFoundException;
import org.mojave.core.wallet.contract.exception.WalletExceptionResolver;
import org.mojave.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import org.mojave.core.wallet.contract.exception.position.PositionLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class ReservePositionCommandRequestor implements ReservePositionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservePositionCommandRequestor.class);

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public ReservePositionCommandRequestor(final Connection connection,
                final ObjectMapper objectMapper) {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.connection = connection;
        this.objectMapper = objectMapper;
    }

    @Override
    public Output execute(final Input input)
        throws PositionLimitExceededException,
               NoPositionUpdateForTransactionException,
               WalletNotFoundException {

        LOGGER.info("ReservePositionCommandRequestor : input: ({})", ObjectLogger.log(input));

        try {

            final var output = NatsRequestor.request(
                this.connection, ReservePositionCommand.SUBJECT_NAME,
                input, Output.class, this.objectMapper);

            LOGGER.info("ReservePositionCommandRequestor : output : ({})", ObjectLogger.log(output));

            return output;

        } catch (final NatsRequestor.InvocationException exception) {

            final var decodedErrorResponse = exception.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof final MojaveErrorResponse errorResponse) {

                final var throwable = WalletExceptionResolver.resolve(errorResponse);

                if (throwable instanceof final PositionLimitExceededException positionLimitExceededException) {
                    throw positionLimitExceededException;
                }

                if (throwable instanceof final NoPositionUpdateForTransactionException noPositionUpdateForTransactionException) {
                    throw noPositionUpdateForTransactionException;
                }

                if (throwable instanceof final WalletNotFoundException walletNotFoundException) {
                    throw walletNotFoundException;
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
