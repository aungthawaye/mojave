package org.mojave.core.wallet.intercom.requestor.command.position;

import io.nats.client.Connection;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.NatsRequestor;
import org.mojave.core.wallet.contract.command.position.DecreasePositionCommand;
import org.mojave.core.wallet.contract.exception.WalletExceptionResolver;
import org.mojave.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class DecreasePositionCommandRequestor implements DecreasePositionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecreasePositionCommandRequestor.class);

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public DecreasePositionCommandRequestor(final Connection connection,
                final ObjectMapper objectMapper) {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.connection = connection;
        this.objectMapper = objectMapper;
    }

    @Override
    public Output execute(final Input input) throws NoPositionUpdateForTransactionException {

        LOGGER.info("DecreasePositionCommandRequestor : input: ({})", ObjectLogger.log(input));

        try {

            final var output = NatsRequestor.request(
                this.connection, DecreasePositionCommand.SUBJECT_NAME,
                input, Output.class, this.objectMapper);

            LOGGER.info("DecreasePositionCommandRequestor : output : ({})", ObjectLogger.log(output));

            return output;

        } catch (final NatsRequestor.InvocationException exception) {

            final var decodedErrorResponse = exception.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof final MojaveErrorResponse errorResponse) {

                final var throwable = WalletExceptionResolver.resolve(errorResponse);

                if (throwable instanceof final NoPositionUpdateForTransactionException noPositionUpdateForTransactionException) {
                    throw noPositionUpdateForTransactionException;
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
