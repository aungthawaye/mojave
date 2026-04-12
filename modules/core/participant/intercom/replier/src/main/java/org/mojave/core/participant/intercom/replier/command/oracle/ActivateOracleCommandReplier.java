package org.mojave.core.participant.intercom.replier.command.oracle;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.Envelope;
import org.mojave.core.participant.contract.command.oracle.ActivateOracleCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class ActivateOracleCommandReplier {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivateOracleCommandReplier.class);

    private final ActivateOracleCommand activateOracleCommand;

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public ActivateOracleCommandReplier(final ActivateOracleCommand activateOracleCommand,
                            final Connection connection,
                            final ObjectMapper objectMapper) {

        Objects.requireNonNull(activateOracleCommand);
        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.activateOracleCommand = activateOracleCommand;
        this.connection = connection;
        this.objectMapper = objectMapper;

        this.connection.createDispatcher(this::handle).subscribe(ActivateOracleCommand.SUBJECT_NAME);
    }

    private void handle(final Message message) {

        final var replyTo = message.getReplyTo();

        if (replyTo == null || replyTo.isBlank()) {
            LOGGER.warn("ActivateOracleCommandReplier : reply subject is missing.");
            return;
        }

        try {

            final var input = this.objectMapper.readValue(
                message.getData(), ActivateOracleCommand.Input.class);

            LOGGER.info("ActivateOracleCommandReplier : input: ({})", ObjectLogger.log(input));

            final var output = this.activateOracleCommand.execute(input);
            final var response = Envelope.success(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);

            LOGGER.info("ActivateOracleCommandReplier : output : ({})", ObjectLogger.log(output));

        } catch (final Exception exception) {

            final var output = MojaveErrorResponse.from(exception);
            final var response = Envelope.failure(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);
        }
    }

}
