package io.mojaloop.core.transaction.consumer;

import io.mojaloop.component.kafka.KafkaTopicConfigurer;
import io.mojaloop.core.transaction.domain.TransactionDomainConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = {"io.mojaloop.core.transaction.consumer"})
@Import(value = {TransactionDomainConfiguration.class})
public class TransactionConsumerConfiguration {

    public interface RequiredBeans extends TransactionDomainConfiguration.RequiredBeans { }

    public interface RequiredSettings extends TransactionDomainConfiguration.RequiredSettings {

        KafkaTopicConfigurer.TopicSettings topicSettings();

    }

}
