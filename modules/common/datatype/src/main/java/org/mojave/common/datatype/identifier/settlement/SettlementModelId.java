package org.mojave.common.datatype.identifier.settlement;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.jspecify.annotations.NonNull;
import org.mojave.component.misc.ddd.LongId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.exc.InvalidFormatException;

@JsonDeserialize(using = SettlementModelId.Deserializer.class)
public class SettlementModelId extends LongId {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public SettlementModelId(long id) {

        super(id);
    }

    public static class Deserializer extends ValueDeserializer<SettlementModelId> {

        @Override
        public SettlementModelId deserialize(JsonParser p, DeserializationContext ctx)
            throws JacksonException {

            var field = p.currentName();
            var text = p.getValueAsString();

            if (text == null || text.isBlank()) {
                return null;
            }

            try {
                return new SettlementModelId(Long.parseLong(text));
            } catch (NumberFormatException e) {
                throw InvalidFormatException.from(
                    p, "'" + field + "' has invalid format. Must be number.", e);
            }
        }

    }

    @Component
    public static class ParamConverter implements Converter<String, SettlementModelId> {

        @Override
        public SettlementModelId convert(final @NonNull String source) {

            return new SettlementModelId(Long.parseLong(source));
        }

    }

}
