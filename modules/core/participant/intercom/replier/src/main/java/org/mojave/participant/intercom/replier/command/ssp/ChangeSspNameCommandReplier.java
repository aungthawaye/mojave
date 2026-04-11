package org.mojave.participant.intercom.replier.command.ssp;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.CommandResponse;
import org.mojave.participant.contract.command.ssp.ChangeSspNameCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class ChangeSspNameCommandReplier {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeSspNameCommandReplier.class);

    private final ChangeSspNameCommand changeSspNameCommand;

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public ChangeSspNameCommandReplier(final ChangeSspNameCommand changeSspNameCommand,
                            final Connection connection,
                            final ObjectMapper objectMapper) {

        Objects.requireNonNull(changeSspNameCommand);
        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.changeSspNameCommand = changeSspNameCommand;
        this.connection = connection;
        this.objectMapper = objectMapper;

        this.connection.createDispatcher(this::handle).subscribe(ChangeSspNameCommand.SUBJECT_NAME);
    }

    private void handle(final Message message) {

        final var replyTo = message.getReplyTo();

        if (replyTo == null || replyTo.isBlank()) {
            LOGGER.warn("ChangeSspNameCommandReplier : reply subject is missing.");
            return;
        }

        try {

            final var input = this.objectMapper.readValue(
                message.getData(), ChangeSspNameCommand.Input.class);

            LOGGER.info("ChangeSspNameCommandReplier : input: ({})", ObjectLogger.log(input));

            final var output = this.changeSspNameCommand.execute(input);
            final var response = CommandResponse.success(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);

            LOGGER.info("ChangeSspNameCommandReplier : output : ({})", ObjectLogger.log(output));

        } catch (final Exception exception) {

            final var output = MojaveErrorResponse.from(exception);
            final var response = CommandResponse.failure(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);
        }
    }

}
