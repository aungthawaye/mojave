package io.mojaloop.component.misc.logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class ObjectLoggerInitializer {

    public ObjectLoggerInitializer(ObjectMapper objectMapper) {

        ObjectLogger.init(objectMapper);
    }
}
