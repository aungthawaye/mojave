package org.mojave.core.participant.intercom.replier.command.fsp;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.CommandResponse;
import org.mojave.core.participant.contract.command.fsp.AddEndpointCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class AddEndpointCommandReplier {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddEndpointCommandReplier.class);

    private final AddEndpointCommand addEndpointCommand;

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public AddEndpointCommandReplier(final AddEndpointCommand addEndpointCommand,
                            final Connection connection,
                            final ObjectMapper objectMapper) {

        Objects.requireNonNull(addEndpointCommand);
        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.addEndpointCommand = addEndpointCommand;
        this.connection = connection;
        this.objectMapper = objectMapper;

        this.connection.createDispatcher(this::handle).subscribe(AddEndpointCommand.SUBJECT_NAME);
    }

    private void handle(final Message message) {

        final var replyTo = message.getReplyTo();

        if (replyTo == null || replyTo.isBlank()) {
            LOGGER.warn("AddEndpointCommandReplier : reply subject is missing.");
            return;
        }

        try {

            final var input = this.objectMapper.readValue(
                message.getData(), AddEndpointCommand.Input.class);

            LOGGER.info("AddEndpointCommandReplier : input: ({})", ObjectLogger.log(input));

            final var output = this.addEndpointCommand.execute(input);
            final var response = CommandResponse.success(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);

            LOGGER.info("AddEndpointCommandReplier : output : ({})", ObjectLogger.log(output));

        } catch (final Exception exception) {

            final var output = MojaveErrorResponse.from(exception);
            final var response = CommandResponse.failure(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);
        }
    }

}
