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
import java.math.BigDecimal;

public class BigDecimalConversion {

    public static class Serializer extends StdSerializer<BigDecimal> {

        public Serializer() {

            super(BigDecimal.class);
        }

        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {

            if (value == null) {
                gen.writeNull();
                return;
            }

            gen.writeString(value.stripTrailingZeros().toPlainString());
        }

    }

    /** Deserialize epochMillis (Long or numeric string) OR ISO-8601 string -> Instant */
    public static class Deserializer extends JsonDeserializer<BigDecimal> {

        @Override
        public BigDecimal deserialize(JsonParser p, DeserializationContext ctx) throws IOException {

            JsonToken t = p.getCurrentToken();

            if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
                // direct number -> epoch millis
                return new BigDecimal(p.getLongValue());
            }

            if (t == JsonToken.VALUE_STRING) {

                String s = p.getText().trim();

                if (s.isEmpty()) {
                    return null;
                }

                try {
                    return new BigDecimal(s);
                } catch (NumberFormatException ignore) {
                    throw ctx.weirdStringException(s, BigDecimal.class, "Expected integer or decimal number");
                }
            }

            if (t == JsonToken.VALUE_NULL) {
                return null;
            }

            throw new JsonMappingException(p, "Expected integer or decimal number");
        }

    }

}
