package org.mojave.wallet.intercom.requestor.command.position;

import io.nats.client.Connection;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.CommandRequestor;
import org.mojave.wallet.contract.command.position.CommitReservationCommand;
import org.mojave.wallet.contract.exception.WalletExceptionResolver;
import org.mojave.wallet.contract.exception.position.FailedToCommitReservationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class CommitReservationCommandRequestor implements CommitReservationCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommitReservationCommandRequestor.class);

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public CommitReservationCommandRequestor(final Connection connection,
                final ObjectMapper objectMapper) {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.connection = connection;
        this.objectMapper = objectMapper;
    }

    @Override
    public Output execute(final Input input) throws FailedToCommitReservationException {

        LOGGER.info("CommitReservationCommandRequestor : input: ({})", ObjectLogger.log(input));

        try {

            final var output = CommandRequestor.request(
                this.connection, CommitReservationCommand.SUBJECT_NAME,
                input, Output.class, this.objectMapper);

            LOGGER.info("CommitReservationCommandRequestor : output : ({})", ObjectLogger.log(output));

            return output;

        } catch (final CommandRequestor.InvocationException exception) {

            final var decodedErrorResponse = exception.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof final MojaveErrorResponse errorResponse) {

                final var throwable = WalletExceptionResolver.resolve(errorResponse);

                if (throwable instanceof final FailedToCommitReservationException failedToCommitReservationException) {
                    throw failedToCommitReservationException;
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
