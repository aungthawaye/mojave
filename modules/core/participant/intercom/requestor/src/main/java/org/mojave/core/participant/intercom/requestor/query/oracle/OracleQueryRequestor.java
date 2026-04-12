package org.mojave.core.participant.intercom.requestor.query.oracle;

import io.nats.client.Connection;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.NatsRequestor;
import org.mojave.core.participant.contract.data.OracleData;
import org.mojave.core.participant.contract.exception.ParticipantExceptionResolver;
import org.mojave.core.participant.contract.query.OracleQuery;
import org.mojave.scheme.rule.enums.participant.PartyIdType;
import org.mojave.scheme.rule.identifier.participant.OracleId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class OracleQueryRequestor implements OracleQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(OracleQueryRequestor.class);

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public OracleQueryRequestor(final Connection connection, final ObjectMapper objectMapper) {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.connection = connection;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<OracleData> find(final PartyIdType type) {

        final var input = new FindByTypeInput(type);

        LOGGER.info("OracleQueryRequestor.find : input: ({})", ObjectLogger.log(input));

        try {

            final var output = NatsRequestor.request(
                this.connection, FIND_BY_TYPE_SUBJECT_NAME, input,
                OracleData.class, this.objectMapper);

            LOGGER.info("OracleQueryRequestor.find : output : ({})", ObjectLogger.log(output));

            return Optional.ofNullable(output);

        } catch (final NatsRequestor.InvocationException exception) {

            throw resolve(exception);
        }
    }

    @Override
    public OracleData get(final PartyIdType type) {

        final var input = new GetByTypeInput(type);

        LOGGER.info("OracleQueryRequestor.get(PartyIdType) : input: ({})", ObjectLogger.log(input));

        try {

            final var output = NatsRequestor.request(
                this.connection, GET_BY_TYPE_SUBJECT_NAME, input,
                OracleData.class, this.objectMapper);

            LOGGER.info(
                "OracleQueryRequestor.get(PartyIdType) : output : ({})", ObjectLogger.log(output));

            return output;

        } catch (final NatsRequestor.InvocationException exception) {

            throw resolve(exception);
        }
    }

    @Override
    public OracleData get(final OracleId oracleId) {

        final var input = new GetByIdInput(oracleId);

        LOGGER.info("OracleQueryRequestor.get(OracleId) : input: ({})", ObjectLogger.log(input));

        try {

            final var output = NatsRequestor.request(
                this.connection, GET_BY_ID_SUBJECT_NAME, input,
                OracleData.class, this.objectMapper);

            LOGGER.info("OracleQueryRequestor.get(OracleId) : output : ({})", ObjectLogger.log(output));

            return output;

        } catch (final NatsRequestor.InvocationException exception) {

            throw resolve(exception);
        }
    }

    @Override
    public List<OracleData> getAll() {

        LOGGER.info("OracleQueryRequestor.getAll : input: ({})", ObjectLogger.log(new GetAllInput()));

        try {

            final var output = NatsRequestor.requestList(
                this.connection, GET_ALL_SUBJECT_NAME, new GetAllInput(),
                OracleData.class, this.objectMapper);

            LOGGER.info("OracleQueryRequestor.getAll : output : ({})", ObjectLogger.log(output));

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
