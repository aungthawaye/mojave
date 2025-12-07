package io.mojaloop.core.transaction.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;

final class TransactionConsumerDependencies
    implements TransactionConsumerConfiguration.RequiredDependencies {

    private final ObjectMapper objectMapper;

    public TransactionConsumerDependencies(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;
    }

}
