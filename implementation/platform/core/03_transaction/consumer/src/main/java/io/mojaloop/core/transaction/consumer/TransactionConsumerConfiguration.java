package io.mojaloop.core.transaction.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.kafka.KafkaConsumerConfigurer;
import io.mojaloop.core.transaction.consumer.listener.AddStepListener;
import io.mojaloop.core.transaction.consumer.listener.CloseTransactionListener;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.contract.command.CloseTransactionCommand;
import io.mojaloop.core.transaction.domain.TransactionDomainConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@EnableKafka
@ComponentScan(basePackages = {"io.mojaloop.core.transaction.consumer"})
@Import(value = {TransactionDomainConfiguration.class})
public class TransactionConsumerConfiguration {

    @Bean(name = AddStepListener.LISTENER_CONTAINER_FACTORY)
    @Qualifier(AddStepListener.QUALIFIER)
    public ConcurrentKafkaListenerContainerFactory<String, AddStepCommand.Input> addStepListenerContainerFactory(AddStepListener.Settings settings, ObjectMapper objectMapper) {

        return KafkaConsumerConfigurer.configure(settings, new KafkaConsumerConfigurer.Deserializer<>() {

            @Override
            public JsonDeserializer<String> forKey() {

                var deserializer = new JsonDeserializer<>(String.class, objectMapper);

                deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                return deserializer;
            }

            @Override
            public JsonDeserializer<AddStepCommand.Input> forValue() {

                var deserializer = new JsonDeserializer<>(AddStepCommand.Input.class, objectMapper);

                deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                return deserializer;
            }
        });
    }

    @Bean(name = CloseTransactionListener.LISTENER_CONTAINER_FACTORY)
    @Qualifier(CloseTransactionListener.QUALIFIER)
    public ConcurrentKafkaListenerContainerFactory<String, CloseTransactionCommand.Input> closeTransactionListenerContainerFactory(CloseTransactionListener.Settings settings,
                                                                                                                                    ObjectMapper objectMapper) {

        return KafkaConsumerConfigurer.configure(settings, new KafkaConsumerConfigurer.Deserializer<>() {

            @Override
            public JsonDeserializer<String> forKey() {

                var deserializer = new JsonDeserializer<>(String.class, objectMapper);

                deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                return deserializer;
            }

            @Override
            public JsonDeserializer<CloseTransactionCommand.Input> forValue() {

                var deserializer = new JsonDeserializer<>(CloseTransactionCommand.Input.class, objectMapper);

                deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                return deserializer;
            }
        });
    }

    public interface RequiredBeans extends TransactionDomainConfiguration.RequiredBeans { }

    public interface RequiredSettings extends TransactionDomainConfiguration.RequiredSettings {

        AddStepListener.Settings addStepListenerSettings();

        CloseTransactionListener.Settings closeTransactionListenerSettings();

    }

}
