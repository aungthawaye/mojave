package io.mojaloop.component.misc.logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectLoggerInitializer {

    public ObjectLoggerInitializer(ObjectMapper objectMapper) {

        ObjectLogger.init(objectMapper);
    }

}
