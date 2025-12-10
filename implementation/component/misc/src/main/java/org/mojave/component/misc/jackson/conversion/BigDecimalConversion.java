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

package org.mojave.component.misc.jackson.conversion;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;

import java.math.BigDecimal;

public class BigDecimalConversion {

    public static class Serializer extends ValueSerializer<BigDecimal> {

        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializationContext ctxt)
            throws JacksonException {

            if (value == null) {
                gen.writeNull();
                return;
            }

            gen.writeString(value.stripTrailingZeros().toPlainString());
        }

    }

    public static class Deserializer extends ValueDeserializer<BigDecimal> {

        @Override
        public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt)
            throws JacksonException {

            JsonToken t = p.currentToken();

            if (t == JsonToken.VALUE_NUMBER_INT) {
                // direct number -> epoch millis
                return new BigDecimal(p.getLongValue());
            }

            if (t == JsonToken.VALUE_NUMBER_FLOAT) {

                return new BigDecimal(p.getString());
            }

            if (t == JsonToken.VALUE_STRING) {

                String s = p.getString().trim();

                if (s.isEmpty()) {
                    return null;
                }

                try {
                    return new BigDecimal(s);
                } catch (NumberFormatException ignore) {
                    throw ctxt.weirdStringException(
                        s, BigDecimal.class, "Expected integer or decimal number");
                }
            }

            if (t == JsonToken.VALUE_NULL) {
                return null;
            }

            throw DatabindException.from(p, "Expected integer or decimal number");
        }

    }

}
