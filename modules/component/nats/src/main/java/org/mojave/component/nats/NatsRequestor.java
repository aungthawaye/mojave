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
import lombok.Getter;
import org.mojave.component.misc.error.MojaveErrorResponse;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public final class NatsRequestor {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);

    private static boolean isWrappedResponse(final JsonNode root) {

        return root != null && root.isObject() && root.has("success");
    }

    private static boolean isSuccessful(final JsonNode root) throws InvocationException {

        final var successNode = root.get("success");

        if (successNode == null || successNode.isNull()) {
            throw new InvocationException(
                new IllegalStateException("NATS response success field is missing."));
        }

        if (successNode.isBoolean()) {
            return successNode.booleanValue();
        }

        if (successNode.isString()) {
            return Boolean.parseBoolean(successNode.stringValue());
        }

        throw new InvocationException(
            new IllegalStateException("NATS response success field is invalid."));
    }

    public static <I, O> O request(final Connection connection,
                                   final String subject,
                                   final I input,
                                   final Class<O> outputType,
                                   final ObjectMapper objectMapper) throws InvocationException {

        Objects.requireNonNull(outputType);

        return request(
            connection, subject, input, objectMapper.constructType(outputType), objectMapper);
    }

    public static <I, O> List<O> requestList(final Connection connection,
                                             final String subject,
                                             final I input,
                                             final Class<O> outputType,
                                             final ObjectMapper objectMapper)
        throws InvocationException {

        Objects.requireNonNull(outputType);

        return request(
            connection, subject, input,
            objectMapper.getTypeFactory().constructCollectionType(List.class, outputType),
            objectMapper);
    }

    public static <I, O> O request(final Connection connection,
                                   final String subject,
                                   final I input,
                                   final JavaType outputType,
                                   final ObjectMapper objectMapper) throws InvocationException {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(subject);
        Objects.requireNonNull(outputType);
        Objects.requireNonNull(objectMapper);

        try {

            final var inputData = objectMapper.writeValueAsBytes(input);
            final var reply = connection.request(subject, inputData, REQUEST_TIMEOUT);

            if (reply == null) {
                throw new InvocationException(
                    new RuntimeException("No response returned from subject " + subject));
            }

            if (reply.isStatusMessage()) {
                throw new InvocationException(null, reply.getStatus().getMessageWithCode());
            }

            final var root = objectMapper.readTree(reply.getData());

            if (!isWrappedResponse(root)) {
                return objectMapper.readValue(
                    objectMapper.treeAsTokens(root), outputType);
            }

            if (!isSuccessful(root)) {

                final MojaveErrorResponse decodedErrorResponse;
                final String originalErrorMessage;
                final var payload = root.get("payload");

                if (payload != null && !payload.isNull()) {

                    decodedErrorResponse = objectMapper.treeToValue(
                        payload, MojaveErrorResponse.class);
                    originalErrorMessage = decodedErrorResponse.message();

                } else {

                    decodedErrorResponse = null;
                    originalErrorMessage = null;
                }

                throw new InvocationException(decodedErrorResponse, originalErrorMessage);
            }

            final var payload = root.get("payload");

            if (payload == null || payload.isNull()) {
                return null;
            }

            return objectMapper.readValue(
                objectMapper.treeAsTokens(payload), outputType);

        } catch (final InvocationException exception) {

            throw exception;

        } catch (final Exception exception) {

            throw new InvocationException(exception);
        }
    }

    @Getter
    public static final class InvocationException extends Exception {

        private final Object decodedErrorResponse;

        private final String originalErrorMessage;

        public InvocationException(final Object decodedErrorResponse,
                                   final String originalErrorMessage) {

            super();

            this.decodedErrorResponse = decodedErrorResponse;
            this.originalErrorMessage = originalErrorMessage;
        }

        public InvocationException(final Throwable cause) {

            super(cause);

            this.decodedErrorResponse = null;
            this.originalErrorMessage = cause.getMessage();
        }

    }

}
