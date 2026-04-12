package org.mojave.core.participant.intercom.requestor.query.ssp;

import io.nats.client.Connection;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.NatsRequestor;
import org.mojave.core.participant.contract.data.SspData;
import org.mojave.core.participant.contract.exception.ParticipantExceptionResolver;
import org.mojave.core.participant.contract.query.SspQuery;
import org.mojave.scheme.rule.identifier.participant.SspId;
import org.mojave.scheme.rule.type.participant.SspCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

@Component
public class SspQueryRequestor implements SspQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(SspQueryRequestor.class);

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public SspQueryRequestor(final Connection connection, final ObjectMapper objectMapper) {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.connection = connection;
        this.objectMapper = objectMapper;
    }

    @Override
    public SspData get(final SspId sspId) {

        final var input = new GetByIdInput(sspId);

        LOGGER.info("SspQueryRequestor.get(SspId) : input: ({})", ObjectLogger.log(input));

        try {

            final var output = NatsRequestor.request(
                this.connection, GET_BY_ID_SUBJECT_NAME, input, SspData.class, this.objectMapper);

            LOGGER.info("SspQueryRequestor.get(SspId) : output : ({})", ObjectLogger.log(output));

            return output;

        } catch (final NatsRequestor.InvocationException exception) {

            throw resolve(exception);
        }
    }

    @Override
    public SspData get(final SspCode sspCode) {

        final var input = new GetByCodeInput(sspCode);

        LOGGER.info("SspQueryRequestor.get(SspCode) : input: ({})", ObjectLogger.log(input));

        try {

            final var output = NatsRequestor.request(
                this.connection, GET_BY_CODE_SUBJECT_NAME, input, SspData.class, this.objectMapper);

            LOGGER.info("SspQueryRequestor.get(SspCode) : output : ({})", ObjectLogger.log(output));

            return output;

        } catch (final NatsRequestor.InvocationException exception) {

            throw resolve(exception);
        }
    }

    @Override
    public List<SspData> getAll() {

        LOGGER.info("SspQueryRequestor.getAll : input: ({})", ObjectLogger.log(new GetAllInput()));

        try {

            final var output = NatsRequestor.requestList(
                this.connection, GET_ALL_SUBJECT_NAME, new GetAllInput(),
                SspData.class, this.objectMapper);

            LOGGER.info("SspQueryRequestor.getAll : output : ({})", ObjectLogger.log(output));

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
