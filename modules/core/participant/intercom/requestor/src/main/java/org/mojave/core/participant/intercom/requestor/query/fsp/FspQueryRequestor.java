package org.mojave.core.participant.intercom.requestor.query.fsp;

import io.nats.client.Connection;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.NatsRequestor;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.participant.contract.exception.ParticipantExceptionResolver;
import org.mojave.core.participant.contract.query.FspQuery;
import org.mojave.scheme.rule.identifier.participant.FspId;
import org.mojave.scheme.rule.type.participant.FspCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

@Component
public class FspQueryRequestor implements FspQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspQueryRequestor.class);

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public FspQueryRequestor(final Connection connection, final ObjectMapper objectMapper) {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.connection = connection;
        this.objectMapper = objectMapper;
    }

    @Override
    public FspData get(final FspId fspId) {

        final var input = new GetByIdInput(fspId);

        LOGGER.info("FspQueryRequestor.get(FspId) : input: ({})", ObjectLogger.log(input));

        try {

            final var output = NatsRequestor.request(
                this.connection, GET_BY_ID_SUBJECT_NAME, input, FspData.class, this.objectMapper);

            LOGGER.info("FspQueryRequestor.get(FspId) : output : ({})", ObjectLogger.log(output));

            return output;

        } catch (final NatsRequestor.InvocationException exception) {

            throw resolve(exception);
        }
    }

    @Override
    public FspData get(final FspCode fspCode) {

        final var input = new GetByCodeInput(fspCode);

        LOGGER.info("FspQueryRequestor.get(FspCode) : input: ({})", ObjectLogger.log(input));

        try {

            final var output = NatsRequestor.request(
                this.connection, GET_BY_CODE_SUBJECT_NAME, input, FspData.class, this.objectMapper);

            LOGGER.info("FspQueryRequestor.get(FspCode) : output : ({})", ObjectLogger.log(output));

            return output;

        } catch (final NatsRequestor.InvocationException exception) {

            throw resolve(exception);
        }
    }

    @Override
    public List<FspData> getAll() {

        LOGGER.info("FspQueryRequestor.getAll : input: ({})", ObjectLogger.log(new GetAllInput()));

        try {

            final var output = NatsRequestor.requestList(
                this.connection, GET_ALL_SUBJECT_NAME, new GetAllInput(),
                FspData.class, this.objectMapper);

            LOGGER.info("FspQueryRequestor.getAll : output : ({})", ObjectLogger.log(output));

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
