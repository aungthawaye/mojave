/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===
 */

package org.mojave.accounting.intercom.consumer.command.chart;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.JetStream;
import io.nats.client.Message;
import io.nats.client.PushSubscribeOptions;
import io.nats.client.api.AckPolicy;
import io.nats.client.api.ConsumerConfiguration;
import org.mojave.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.accounting.contract.constant.AccountingStreamName;
import org.mojave.component.nats.JetStreamConfigurer;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class CreateCoaConsumer {

    public static final String CONSUMER_NAME = "accounting:create-coa-consumer";

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateCoaConsumer.class);

    private final CreateCoaCommand createCoaCommand;

    private final ObjectMapper objectMapper;

    private final Dispatcher dispatcher;

    public CreateCoaConsumer(final CreateCoaCommand createCoaCommand,
                             final Connection connection,
                             final ObjectMapper objectMapper) {

        Objects.requireNonNull(createCoaCommand);
        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.createCoaCommand = createCoaCommand;
        this.objectMapper = objectMapper;
        this.dispatcher = connection.createDispatcher(this::handle);

        this.subscribe(connection);
    }

    private void handle(final Message message) {

        try {

            final var input = this.objectMapper.readValue(
                message.getData(), CreateCoaCommand.Input.class);

            this.createCoaCommand.execute(input);
            message.ack();

        } catch (final UncheckedDomainException exception) {

            LOGGER.error("CreateCoaConsumer business error:", exception);
            message.ack();

        } catch (final Exception exception) {

            LOGGER.error("CreateCoaConsumer processing error:", exception);
            message.nak();
        }
    }

    private void subscribe(final Connection connection) {

        try {

            JetStreamConfigurer.ensureStream(
                connection, AccountingStreamName.STREAM_NAME, CreateCoaCommand.TOPIC_NAME);

            final JetStream jetStream = connection.jetStream();
            final var consumerConfiguration = ConsumerConfiguration
                                                  .builder()
                                                  .durable(CONSUMER_NAME)
                                                  .ackPolicy(AckPolicy.Explicit)
                                                  .filterSubject(CreateCoaCommand.TOPIC_NAME)
                                                  .build();

            final PushSubscribeOptions subscribeOptions = PushSubscribeOptions
                                                              .builder()
                                                              .stream(AccountingStreamName.STREAM_NAME)
                                                              .configuration(consumerConfiguration)
                                                              .build();

            jetStream.subscribe(
                CreateCoaCommand.TOPIC_NAME, this.dispatcher, this::handle, false,
                subscribeOptions);

        } catch (final Exception exception) {

            throw new RuntimeException(exception);
        }
    }

}
