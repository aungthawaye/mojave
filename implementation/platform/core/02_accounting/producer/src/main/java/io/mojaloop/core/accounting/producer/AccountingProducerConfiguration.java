package io.mojaloop.core.accounting.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.kafka.KafkaProducerConfigurer;
import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.core.accounting.contract.command.ledger.PostLedgerFlowCommand;
import io.mojaloop.core.accounting.producer.publisher.PostLedgerFlowPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@ComponentScan(basePackages = {"io.mojaloop.core.accounting.producer"})
@Import(value = {MiscConfiguration.class})
public class AccountingProducerConfiguration {

    @Bean
    @Qualifier(PostLedgerFlowPublisher.QUALIFIER)
    public KafkaTemplate<String, PostLedgerFlowCommand.Input> addStepKafkaTemplate(@Qualifier(
        PostLedgerFlowPublisher.QUALIFIER) ProducerFactory<String, PostLedgerFlowCommand.Input> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Qualifier(PostLedgerFlowPublisher.QUALIFIER)
    public ProducerFactory<String, PostLedgerFlowCommand.Input> addStepProducerFactory(
        KafkaProducerConfigurer.ProducerSettings settings,
        ObjectMapper objectMapper) {

        return KafkaProducerConfigurer.configure(
            settings.bootstrapServers(), settings.ack(),
            new KafkaProducerConfigurer.Serializer<>() {

                @Override
                public JsonSerializer<String> forKey() {

                    return new JsonSerializer<>(objectMapper);
                }

                @Override
                public JsonSerializer<PostLedgerFlowCommand.Input> forValue() {

                    return new JsonSerializer<>(objectMapper);
                }
            });
    }

}
