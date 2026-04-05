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

package org.mojave.component.nats;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.api.PublishAck;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Objects;

public final class CommandPublisher {

    public static <I> PublishAck publish(final Connection connection,
                                         final String subject,
                                         final I input,
                                         final ObjectMapper objectMapper) {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(subject);
        Objects.requireNonNull(objectMapper);

        try {

            final var jetStream = connection.jetStream();
            final var payload = objectMapper.writeValueAsBytes(input);

            return jetStream.publish(subject, payload);

        } catch (final IOException | JetStreamApiException exception) {

            throw new RuntimeException(exception);
        }
    }

    private CommandPublisher() {

    }

}
