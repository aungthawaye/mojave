package org.mojave.wallet.intercom.replier.command.position;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.CommandResponse;
import org.mojave.wallet.contract.command.position.CommitReservationCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class CommitReservationReplier {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommitReservationReplier.class);

    private final CommitReservationCommand command;

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public CommitReservationReplier(final CommitReservationCommand command,
                final Connection connection,
                final ObjectMapper objectMapper) {

        Objects.requireNonNull(command);
        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.command = command;
        this.connection = connection;
        this.objectMapper = objectMapper;

        this.connection.createDispatcher(this::handle).subscribe(CommitReservationCommand.SUBJECT_NAME);
    }

    private void handle(final Message message) {

        final var replyTo = message.getReplyTo();

        if (replyTo == null || replyTo.isBlank()) {
            LOGGER.warn("CommitReservationReplier : reply subject is missing.");
            return;
        }

        try {

            final var input = this.objectMapper.readValue(
                message.getData(), CommitReservationCommand.Input.class);

            LOGGER.info("CommitReservationReplier : input: ({})", ObjectLogger.log(input));

            final var output = this.command.execute(input);
            final var response = CommandResponse.success(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);

            LOGGER.info("CommitReservationReplier : output : ({})", ObjectLogger.log(output));

        } catch (final Exception exception) {

            final var output = MojaveErrorResponse.from(exception);
            final var response = CommandResponse.failure(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);
        }
    }

}
