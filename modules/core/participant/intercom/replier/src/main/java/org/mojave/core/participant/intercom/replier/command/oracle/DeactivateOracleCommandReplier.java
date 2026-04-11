package org.mojave.core.participant.intercom.replier.command.oracle;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.CommandResponse;
import org.mojave.core.participant.contract.command.oracle.DeactivateOracleCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class DeactivateOracleCommandReplier {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeactivateOracleCommandReplier.class);

    private final DeactivateOracleCommand deactivateOracleCommand;

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public DeactivateOracleCommandReplier(final DeactivateOracleCommand deactivateOracleCommand,
                            final Connection connection,
                            final ObjectMapper objectMapper) {

        Objects.requireNonNull(deactivateOracleCommand);
        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.deactivateOracleCommand = deactivateOracleCommand;
        this.connection = connection;
        this.objectMapper = objectMapper;

        this.connection.createDispatcher(this::handle).subscribe(DeactivateOracleCommand.SUBJECT_NAME);
    }

    private void handle(final Message message) {

        final var replyTo = message.getReplyTo();

        if (replyTo == null || replyTo.isBlank()) {
            LOGGER.warn("DeactivateOracleCommandReplier : reply subject is missing.");
            return;
        }

        try {

            final var input = this.objectMapper.readValue(
                message.getData(), DeactivateOracleCommand.Input.class);

            LOGGER.info("DeactivateOracleCommandReplier : input: ({})", ObjectLogger.log(input));

            final var output = this.deactivateOracleCommand.execute(input);
            final var response = CommandResponse.success(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);

            LOGGER.info("DeactivateOracleCommandReplier : output : ({})", ObjectLogger.log(output));

        } catch (final Exception exception) {

            final var output = MojaveErrorResponse.from(exception);
            final var response = CommandResponse.failure(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);
        }
    }

}
