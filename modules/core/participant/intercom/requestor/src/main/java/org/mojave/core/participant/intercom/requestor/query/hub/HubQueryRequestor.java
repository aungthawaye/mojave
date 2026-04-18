package org.mojave.core.participant.intercom.requestor.query.hub;

import io.nats.client.Connection;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.NatsRequestor;
import org.mojave.core.participant.contract.data.HubData;
import org.mojave.core.participant.contract.exception.ParticipantExceptionResolver;
import org.mojave.core.participant.contract.query.HubQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

@Component
public class HubQueryRequestor implements HubQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(HubQueryRequestor.class);

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public HubQueryRequestor(final Connection connection, final ObjectMapper objectMapper) {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.connection = connection;
        this.objectMapper = objectMapper;
    }

    @Override
    public long count() {

        LOGGER.info("HubQueryRequestor.count : input: ({})", ObjectLogger.log(new CountInput()));

        try {

            final var output = NatsRequestor.request(
                this.connection, COUNT_SUBJECT_NAME, new CountInput(),
                Long.class, this.objectMapper);

            LOGGER.info("HubQueryRequestor.count : output : ({})", ObjectLogger.log(output));

            return output;

        } catch (final NatsRequestor.InvocationException exception) {

            throw resolve(exception);
        }
    }

    @Override
    public HubData get() {

        LOGGER.info("HubQueryRequestor.get : input: ({})", ObjectLogger.log(new GetInput()));

        try {

            final var output = NatsRequestor.request(
                this.connection, GET_SUBJECT_NAME, new GetInput(),
                HubData.class, this.objectMapper);

            LOGGER.info("HubQueryRequestor.get : output : ({})", ObjectLogger.log(output));

            return output;

        } catch (final NatsRequestor.InvocationException exception) {

            throw resolve(exception);
        }
    }

    @Override
    public List<HubData> getAll() {

        LOGGER.info("HubQueryRequestor.getAll : input: ({})", ObjectLogger.log(new GetAllInput()));

        try {

            final var output = NatsRequestor.requestList(
                this.connection, GET_ALL_SUBJECT_NAME, new GetAllInput(),
                HubData.class, this.objectMapper);

            LOGGER.info("HubQueryRequestor.getAll : output : ({})", ObjectLogger.log(output));

            return output;

        } catch (final NatsRequestor.InvocationException exception) {

            throw resolve(exception);
        }
    }

    private static RuntimeException resolve(final NatsRequestor.InvocationException exception) {

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

        return new RuntimeException(exception);
    }

}
