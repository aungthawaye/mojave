package org.mojave.core.participant.intercom.replier.command.ssp;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.Envelope;
import org.mojave.core.participant.contract.command.ssp.DeactivateSspCurrencyCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class DeactivateSspCurrencyCommandReplier {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeactivateSspCurrencyCommandReplier.class);

    private final DeactivateSspCurrencyCommand deactivateSspCurrencyCommand;

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public DeactivateSspCurrencyCommandReplier(final DeactivateSspCurrencyCommand deactivateSspCurrencyCommand,
                            final Connection connection,
                            final ObjectMapper objectMapper) {

        Objects.requireNonNull(deactivateSspCurrencyCommand);
        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.deactivateSspCurrencyCommand = deactivateSspCurrencyCommand;
        this.connection = connection;
        this.objectMapper = objectMapper;

        this.connection.createDispatcher(this::handle).subscribe(DeactivateSspCurrencyCommand.SUBJECT_NAME);
    }

    private void handle(final Message message) {

        final var replyTo = message.getReplyTo();

        if (replyTo == null || replyTo.isBlank()) {
            LOGGER.warn("DeactivateSspCurrencyCommandReplier : reply subject is missing.");
            return;
        }

        try {

            final var input = this.objectMapper.readValue(
                message.getData(), DeactivateSspCurrencyCommand.Input.class);

            LOGGER.info("DeactivateSspCurrencyCommandReplier : input: ({})", ObjectLogger.log(input));

            final var output = this.deactivateSspCurrencyCommand.execute(input);
            final var response = Envelope.success(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);

            LOGGER.info("DeactivateSspCurrencyCommandReplier : output : ({})", ObjectLogger.log(output));

        } catch (final Exception exception) {

            final var output = MojaveErrorResponse.from(exception);
            final var response = Envelope.failure(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);
        }
    }

}
