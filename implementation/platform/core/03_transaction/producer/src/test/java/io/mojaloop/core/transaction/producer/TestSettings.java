package io.mojaloop.core.transaction.producer;

import io.mojaloop.component.kafka.KafkaProducerConfigurer;
import org.springframework.context.annotation.Bean;

public class TestSettings implements TransactionProducerConfiguration.RequiredSettings {

    @Bean
    @Override
    public KafkaProducerConfigurer.ProducerSettings producerSettings() {

        return new KafkaProducerConfigurer.ProducerSettings("localhost:9092", "all");
    }

}
