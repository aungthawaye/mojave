package io.mojaloop.component.misc.jackson.conversion;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeParseException;

public class InstantConversion {

    public static class Serializer extends StdSerializer<Instant> {

        public Serializer() {

            super(Instant.class);
        }

        @Override
        public void serialize(Instant value, JsonGenerator gen, SerializerProvider provider) throws IOException {

            if (value == null) {
                gen.writeNull();
                return;
            }

            gen.writeString(String.valueOf(value.getEpochSecond()));
        }

    }

    /** Deserialize epochMillis (Long or numeric string) OR ISO-8601 string -> Instant */
    public static class Deserializer extends JsonDeserializer<Instant> {

        @Override
        public Instant deserialize(JsonParser p, DeserializationContext ctx) throws IOException {

            JsonToken t = p.getCurrentToken();

            if (t == JsonToken.VALUE_NUMBER_INT) {
                // direct number -> epoch millis
                return Instant.ofEpochSecond(p.getLongValue());
            }

            if (t == JsonToken.VALUE_STRING) {

                String s = p.getText().trim();

                if (s.isEmpty()) {
                    return null;
                }

                // Try numeric string first (epoch millis)
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
                    throw ctx.weirdStringException(
                        s, Instant.class, "Expected epoch millis (long) or ISO-8601 instant");
                }
            }

            if (t == JsonToken.VALUE_NULL) {
                return null;
            }

            throw new JsonMappingException(p, "Expected epoch millis (long) or ISO-8601 instant");
        }

    }

}
