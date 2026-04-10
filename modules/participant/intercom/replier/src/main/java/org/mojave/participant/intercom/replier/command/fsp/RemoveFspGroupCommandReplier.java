package org.mojave.participant.intercom.replier.command.fsp;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.CommandResponse;
import org.mojave.participant.contract.command.fsp.RemoveFspGroupCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class RemoveFspGroupCommandReplier {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveFspGroupCommandReplier.class);

    private final RemoveFspGroupCommand removeFspGroupCommand;

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public RemoveFspGroupCommandReplier(final RemoveFspGroupCommand removeFspGroupCommand,
                            final Connection connection,
                            final ObjectMapper objectMapper) {

        Objects.requireNonNull(removeFspGroupCommand);
        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.removeFspGroupCommand = removeFspGroupCommand;
        this.connection = connection;
        this.objectMapper = objectMapper;

        this.connection.createDispatcher(this::handle).subscribe(RemoveFspGroupCommand.SUBJECT_NAME);
    }

    private void handle(final Message message) {

        final var replyTo = message.getReplyTo();

        if (replyTo == null || replyTo.isBlank()) {
            LOGGER.warn("RemoveFspGroupCommandReplier : reply subject is missing.");
            return;
        }

        try {

            final var input = this.objectMapper.readValue(
                message.getData(), RemoveFspGroupCommand.Input.class);

            LOGGER.info("RemoveFspGroupCommandReplier : input: ({})", ObjectLogger.log(input));

            final var output = this.removeFspGroupCommand.execute(input);
            final var response = CommandResponse.success(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);

            LOGGER.info("RemoveFspGroupCommandReplier : output : ({})", ObjectLogger.log(output));

        } catch (final Exception exception) {

            final var output = MojaveErrorResponse.from(exception);
            final var response = CommandResponse.failure(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);
        }
    }

}
