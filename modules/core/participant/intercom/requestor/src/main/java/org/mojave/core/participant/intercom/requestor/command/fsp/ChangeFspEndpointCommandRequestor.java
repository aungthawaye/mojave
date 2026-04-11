package org.mojave.core.participant.intercom.requestor.command.fsp;

import io.nats.client.Connection;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.CommandRequestor;
import org.mojave.core.participant.contract.command.fsp.ChangeFspEndpointCommand;
import org.mojave.core.participant.contract.exception.ParticipantExceptionResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class ChangeFspEndpointCommandRequestor implements ChangeFspEndpointCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeFspEndpointCommandRequestor.class);

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public ChangeFspEndpointCommandRequestor(final Connection connection, final ObjectMapper objectMapper) {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.connection = connection;
        this.objectMapper = objectMapper;
    }

    @Override
    public Output execute(final Input input) {

        LOGGER.info("ChangeFspEndpointCommandRequestor : input: ({})", ObjectLogger.log(input));

        try {

            final var output = CommandRequestor.request(
                this.connection, ChangeFspEndpointCommand.SUBJECT_NAME,
                input, Output.class, this.objectMapper);

            LOGGER.info("ChangeFspEndpointCommandRequestor : output : ({})", ObjectLogger.log(output));

            return output;

        } catch (final CommandRequestor.InvocationException exception) {

            final var decodedErrorResponse = exception.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof final MojaveErrorResponse errorResponse) {

                final var throwable = ParticipantExceptionResolver.resolve(errorResponse);

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
