package org.mojave.core.participant.intercom.replier.command.oracle;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.Envelope;
import org.mojave.core.participant.contract.command.oracle.CreateOracleCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class CreateOracleCommandReplier {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateOracleCommandReplier.class);

    private final CreateOracleCommand createOracleCommand;

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public CreateOracleCommandReplier(final CreateOracleCommand createOracleCommand,
                            final Connection connection,
                            final ObjectMapper objectMapper) {

        Objects.requireNonNull(createOracleCommand);
        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.createOracleCommand = createOracleCommand;
        this.connection = connection;
        this.objectMapper = objectMapper;

        this.connection.createDispatcher(this::handle).subscribe(CreateOracleCommand.SUBJECT_NAME);
    }

    private void handle(final Message message) {

        final var replyTo = message.getReplyTo();

        if (replyTo == null || replyTo.isBlank()) {
            LOGGER.warn("CreateOracleCommandReplier : reply subject is missing.");
            return;
        }

        try {

            final var input = this.objectMapper.readValue(
                message.getData(), CreateOracleCommand.Input.class);

            LOGGER.info("CreateOracleCommandReplier : input: ({})", ObjectLogger.log(input));

            final var output = this.createOracleCommand.execute(input);
            final var response = Envelope.success(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);

            LOGGER.info("CreateOracleCommandReplier : output : ({})", ObjectLogger.log(output));

        } catch (final Exception exception) {

            final var output = MojaveErrorResponse.from(exception);
            final var response = Envelope.failure(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);
        }
    }

}
