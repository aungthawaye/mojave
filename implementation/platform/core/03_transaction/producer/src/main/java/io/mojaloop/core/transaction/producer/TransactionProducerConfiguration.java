package io.mojaloop.core.transaction.producer;

import io.mojaloop.component.kafka.KafkaProducerConfigurer;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.contract.command.CommitTransactionCommand;
import io.mojaloop.core.transaction.producer.publisher.AddStepPublisher;
import io.mojaloop.core.transaction.producer.publisher.CommitTransactionPublisher;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;

@ComponentScan(basePackages = {"io.mojaloop.core.transaction.producer"})
public class TransactionProducerConfiguration {

    @Bean
    @Qualifier(AddStepPublisher.QUALIFIER)
    public KafkaTemplate<String, AddStepCommand.Input> addStepKafkaTemplate(@Qualifier(AddStepPublisher.QUALIFIER) ProducerFactory<String, AddStepCommand.Input> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Qualifier(AddStepPublisher.QUALIFIER)
    public ProducerFactory<String, AddStepCommand.Input> addStepProducer(KafkaProducerConfigurer.ProducerSettings settings) {

        return KafkaProducerConfigurer.configure(settings.bootstrapServers(), settings.ack());
    }

    @Bean
    @Qualifier(CommitTransactionPublisher.QUALIFIER)
    public KafkaTemplate<String, CommitTransactionCommand.Input> commitTransactionKafkaTemplate(@Qualifier(CommitTransactionPublisher.QUALIFIER) ProducerFactory<String, CommitTransactionCommand.Input> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Qualifier(CommitTransactionPublisher.QUALIFIER)
    public ProducerFactory<String, CommitTransactionCommand.Input> commitTransactionProducer(KafkaProducerConfigurer.ProducerSettings settings) {

        return KafkaProducerConfigurer.configure(settings.bootstrapServers(), settings.ack());
    }

    @Bean
    public KafkaAdmin kafkaAdmin(KafkaProducerConfigurer.ProducerSettings settings) {

        var cfg = new HashMap<String, Object>();

        cfg.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, settings.bootstrapServers());

        return new KafkaAdmin(cfg);
    }

    public interface RequiredBeans { }

    public interface RequiredSettings {

        KafkaProducerConfigurer.ProducerSettings producerSettings();

    }

}
