package org.mojave.core.participant.intercom.replier.command.hub;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.CommandResponse;
import org.mojave.core.participant.contract.command.hub.AddHubCurrencyCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class AddHubCurrencyCommandReplier {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddHubCurrencyCommandReplier.class);

    private final AddHubCurrencyCommand addHubCurrencyCommand;

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public AddHubCurrencyCommandReplier(final AddHubCurrencyCommand addHubCurrencyCommand,
                            final Connection connection,
                            final ObjectMapper objectMapper) {

        Objects.requireNonNull(addHubCurrencyCommand);
        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.addHubCurrencyCommand = addHubCurrencyCommand;
        this.connection = connection;
        this.objectMapper = objectMapper;

        this.connection.createDispatcher(this::handle).subscribe(AddHubCurrencyCommand.SUBJECT_NAME);
    }

    private void handle(final Message message) {

        final var replyTo = message.getReplyTo();

        if (replyTo == null || replyTo.isBlank()) {
            LOGGER.warn("AddHubCurrencyCommandReplier : reply subject is missing.");
            return;
        }

        try {

            final var input = this.objectMapper.readValue(
                message.getData(), AddHubCurrencyCommand.Input.class);

            LOGGER.info("AddHubCurrencyCommandReplier : input: ({})", ObjectLogger.log(input));

            final var output = this.addHubCurrencyCommand.execute(input);
            final var response = CommandResponse.success(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);

            LOGGER.info("AddHubCurrencyCommandReplier : output : ({})", ObjectLogger.log(output));

        } catch (final Exception exception) {

            final var output = MojaveErrorResponse.from(exception);
            final var response = CommandResponse.failure(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);
        }
    }

}
