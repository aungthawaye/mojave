package io.mojaloop.component.misc.logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectLogger {

    private static ObjectMapper OBJECT_MAPPER = null;

    public static void init(ObjectMapper objectMapper) {

        OBJECT_MAPPER = objectMapper;
    }

    public Object log(Object object) {

        return new LazyObject(object);
    }

    private record LazyObject(Object value) {

        @Override
        public String toString() {

            if (value == null) {
                return "null";
            }

            try {
                return OBJECT_MAPPER.writeValueAsString(value);
            } catch (JsonProcessingException e) {
                return String.valueOf(value);
            }
        }

    }

}
