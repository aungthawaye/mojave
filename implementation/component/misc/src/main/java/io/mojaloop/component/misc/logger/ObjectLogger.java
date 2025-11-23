package io.mojaloop.component.misc.logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectLogger {

    private static ObjectMapper OBJECT_MAPPER = null;

    public static void init(ObjectMapper objectMapper) {

        OBJECT_MAPPER = objectMapper;
    }

    public static Object log(Object object) {

        return new LazyObject(object);
    }

    private record LazyObject(Object value) {

        @Override
        public String toString() {

            switch (value) {
                case null -> {
                    return "null";
                }
                case String s -> {
                    return s;
                }
                case Number number -> {
                    return String.valueOf(value);
                }
                case Boolean b -> {
                    return String.valueOf(value);
                }
                case Enum anEnum -> {
                    return String.valueOf(value);
                }
                case Exception e -> {
                    return e.getMessage();
                }
                default -> {
                    try {
                        return OBJECT_MAPPER.writeValueAsString(value);
                    } catch (JsonProcessingException e) {
                        return String.valueOf(value);
                    }
                }
            }

        }

    }

}
