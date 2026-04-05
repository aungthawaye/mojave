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
import io.nats.client.JetStreamApiException;
import io.nats.client.api.RetentionPolicy;
import io.nats.client.api.StorageType;
import io.nats.client.api.StreamConfiguration;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Objects;

public final class JetStreamConfigurer {

    private JetStreamConfigurer() {

    }

    public static void ensureStream(final Connection connection,
                                    final String streamName,
                                    final String subject) {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(streamName);
        Objects.requireNonNull(subject);

        try {

            final var management = connection.jetStreamManagement();

            if (!management.getStreamNames().contains(streamName)) {

                final var streamConfiguration = StreamConfiguration
                                                    .builder()
                                                    .name(streamName)
                                                    .subjects(subject)
                                                    .storageType(StorageType.File)
                                                    .retentionPolicy(RetentionPolicy.Limits)
                                                    .build();

                management.addStream(streamConfiguration);

                return;
            }

            final var streamInfo = management.getStreamInfo(streamName);
            final var subjects = new LinkedHashSet<>(streamInfo.getConfiguration().getSubjects());

            if (!subjects.add(subject)) {
                return;
            }

            final var streamConfiguration = StreamConfiguration
                                                .builder(streamInfo.getConfiguration())
                                                .subjects(subjects)
                                                .build();

            management.updateStream(streamConfiguration);

        } catch (final IOException | JetStreamApiException exception) {

            throw new RuntimeException(exception);
        }
    }

}
