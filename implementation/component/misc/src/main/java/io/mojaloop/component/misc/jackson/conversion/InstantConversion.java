/*-
 * ================================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
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
 * ================================================================================
 */

package io.mojaloop.component.misc.jackson.conversion;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;

import java.time.Instant;
import java.time.format.DateTimeParseException;

public class InstantConversion {

    public static class Serializer extends ValueSerializer<Instant> {

        @Override
        public void serialize(Instant value, JsonGenerator gen, SerializationContext ctxt)
            throws JacksonException {

            if (value == null) {
                gen.writeNull();
                return;
            }

            var epochSeconds = value.getEpochSecond();

            gen.writeString(String.valueOf(epochSeconds));
        }

    }

    /** Deserialize epoch second (Long or numeric string) OR ISO-8601 string -> Instant */
    public static class Deserializer extends ValueDeserializer<Instant> {

        @Override
        public Instant deserialize(JsonParser p, DeserializationContext ctxt)
            throws JacksonException {

            JsonToken t = p.currentToken();

            if (t == JsonToken.VALUE_NUMBER_INT) {
                // direct number -> epoch seconds
                return Instant.ofEpochSecond(p.getLongValue());
            }

            if (t == JsonToken.VALUE_STRING) {

                String s = p.getString().trim();

                if (s.isEmpty()) {
                    return null;
                }

                // Try numeric string first (epoch second)
                if (s.chars().allMatch(ch -> ch == '-' || Character.isDigit(ch))) {

                    try {
                        return Instant.ofEpochSecond(Long.parseLong(s));
                    } catch (NumberFormatException ignore) {
                        // fall through to ISO parsing
                    }
                }

                // Fall back to ISO-8601 (e.g. "2025-09-25T02:03:04Z")
                try {
                    return Instant.parse(s);
                } catch (DateTimeParseException ex) {
                    throw ctxt.weirdStringException(
                        s, Instant.class, "Expected epoch seconds (long) or ISO-8601 instant");
                }
            }

            if (t == JsonToken.VALUE_NULL) {
                return null;
            }

            throw DatabindException.from(p, "Expected epoch seconds (long) or ISO-8601 instant");
        }

    }

}
