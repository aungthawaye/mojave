package org.mojave.participant.intercom.replier.command.oracle;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.CommandResponse;
import org.mojave.participant.contract.command.oracle.ChangeOracleNameCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class ChangeOracleNameCommandReplier {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeOracleNameCommandReplier.class);

    private final ChangeOracleNameCommand changeOracleNameCommand;

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public ChangeOracleNameCommandReplier(final ChangeOracleNameCommand changeOracleNameCommand,
                            final Connection connection,
                            final ObjectMapper objectMapper) {

        Objects.requireNonNull(changeOracleNameCommand);
        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.changeOracleNameCommand = changeOracleNameCommand;
        this.connection = connection;
        this.objectMapper = objectMapper;

        this.connection.createDispatcher(this::handle).subscribe(ChangeOracleNameCommand.SUBJECT_NAME);
    }

    private void handle(final Message message) {

        final var replyTo = message.getReplyTo();

        if (replyTo == null || replyTo.isBlank()) {
            LOGGER.warn("ChangeOracleNameCommandReplier : reply subject is missing.");
            return;
        }

        try {

            final var input = this.objectMapper.readValue(
                message.getData(), ChangeOracleNameCommand.Input.class);

            LOGGER.info("ChangeOracleNameCommandReplier : input: ({})", ObjectLogger.log(input));

            final var output = this.changeOracleNameCommand.execute(input);
            final var response = CommandResponse.success(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);

            LOGGER.info("ChangeOracleNameCommandReplier : output : ({})", ObjectLogger.log(output));

        } catch (final Exception exception) {

            final var output = MojaveErrorResponse.from(exception);
            final var response = CommandResponse.failure(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);
        }
    }

}
