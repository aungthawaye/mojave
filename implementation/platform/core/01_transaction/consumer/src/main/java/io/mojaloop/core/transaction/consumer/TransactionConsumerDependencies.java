package io.mojaloop.core.transaction.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;

final class TransactionConsumerDependencies
    implements TransactionConsumerConfiguration.RequiredDependencies {

    private final ObjectMapper objectMapper;

    public TransactionConsumerDependencies(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;
    }

    @Bean
    public Something something() {

        return new Something() {

            @Override
            public void doSomething() {

            }
        };
    }

}
