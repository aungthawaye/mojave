package org.mojave.rail.fspiop.transfer.domain;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.mojave.component.kafka.KafkaConsumerConfigurer;
import org.mojave.component.kafka.KafkaProducerConfigurer;
import org.mojave.rail.fspiop.transfer.contract.command.step.stateful.AbortTransferStep;
import org.mojave.rail.fspiop.transfer.contract.command.step.stateful.CommitTransferStep;
import org.mojave.rail.fspiop.transfer.contract.command.step.financial.RollbackReservationStep;
import org.mojave.rail.fspiop.transfer.contract.command.step.stateful.DisputeTransferStep;
import org.mojave.rail.fspiop.transfer.domain.kafka.listener.AbortTransferStepListener;
import org.mojave.rail.fspiop.transfer.domain.kafka.listener.CommitTransferStepListener;
import org.mojave.rail.fspiop.transfer.domain.kafka.listener.DisputeTransferStepListener;
import org.mojave.rail.fspiop.transfer.domain.kafka.listener.RollbackReservationStepListener;
import org.mojave.rail.fspiop.transfer.domain.kafka.publisher.AbortTransferStepPublisher;
import org.mojave.rail.fspiop.transfer.domain.kafka.publisher.CommitTransferStepPublisher;
import org.mojave.rail.fspiop.transfer.domain.kafka.publisher.DisputeTransferStepPublisher;
import org.mojave.rail.fspiop.transfer.domain.kafka.publisher.RollbackReservationStepPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@EnableKafka
public class TransferKafkaConfiguration {

    @Bean
    @Qualifier(AbortTransferStepPublisher.QUALIFIER)
    public KafkaTemplate<String, AbortTransferStep.Input> abortTransferStepKafkaTemplate(
        @Qualifier(AbortTransferStepPublisher.QUALIFIER)
        ProducerFactory<String, AbortTransferStep.Input> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean(name = AbortTransferStepListener.LISTENER_CONTAINER_FACTORY)
    @Qualifier(AbortTransferStepListener.QUALIFIER)
    public ConcurrentKafkaListenerContainerFactory<String, AbortTransferStep.Input> abortTransferStepListenerContainerFactory(
        AbortTransferStepListener.Settings settings,
        ObjectMapper objectMapper) {

        return KafkaConsumerConfigurer.configure(
            settings, new KafkaConsumerConfigurer.Deserializers<>() {

                @Override
                public Deserializer<String> forKey() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        String.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }

                @Override
                public Deserializer<AbortTransferStep.Input> forValue() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        AbortTransferStep.Input.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }
            });
    }

    @Bean
    @Qualifier(AbortTransferStepPublisher.QUALIFIER)
    public ProducerFactory<String, AbortTransferStep.Input> abortTransferStepProducerFactory(
        TransferKafkaConfiguration.ProducerSettings settings,
        ObjectMapper objectMapper) {

        return KafkaProducerConfigurer.configure(
            settings.bootstrapServers(), settings.ack(),
            new KafkaProducerConfigurer.Serializers<>() {

                @Override
                public Serializer<String> forKey() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }

                @Override
                public Serializer<AbortTransferStep.Input> forValue() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }
            });
    }

    @Bean
    @Qualifier(CommitTransferStepPublisher.QUALIFIER)
    public KafkaTemplate<String, CommitTransferStep.Input> commitTransferStepKafkaTemplate(
        @Qualifier(CommitTransferStepPublisher.QUALIFIER)
        ProducerFactory<String, CommitTransferStep.Input> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean(name = CommitTransferStepListener.LISTENER_CONTAINER_FACTORY)
    @Qualifier(CommitTransferStepListener.QUALIFIER)
    public ConcurrentKafkaListenerContainerFactory<String, CommitTransferStep.Input> commitTransferStepListenerContainerFactory(
        CommitTransferStepListener.Settings settings,
        ObjectMapper objectMapper) {

        return KafkaConsumerConfigurer.configure(
            settings, new KafkaConsumerConfigurer.Deserializers<>() {

                @Override
                public Deserializer<String> forKey() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        String.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }

                @Override
                public Deserializer<CommitTransferStep.Input> forValue() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        CommitTransferStep.Input.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }
            });
    }

    @Bean
    @Qualifier(CommitTransferStepPublisher.QUALIFIER)
    public ProducerFactory<String, CommitTransferStep.Input> commitTransferStepProducerFactory(
        TransferKafkaConfiguration.ProducerSettings settings,
        ObjectMapper objectMapper) {

        return KafkaProducerConfigurer.configure(
            settings.bootstrapServers(), settings.ack(),
            new KafkaProducerConfigurer.Serializers<>() {

                @Override
                public Serializer<String> forKey() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }

                @Override
                public Serializer<CommitTransferStep.Input> forValue() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }
            });
    }

    @Bean
    @Qualifier(DisputeTransferStepPublisher.QUALIFIER)
    public KafkaTemplate<String, DisputeTransferStep.Input> disputeTransferStepKafkaTemplate(
        @Qualifier(DisputeTransferStepPublisher.QUALIFIER)
        ProducerFactory<String, DisputeTransferStep.Input> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean(name = DisputeTransferStepListener.LISTENER_CONTAINER_FACTORY)
    @Qualifier(DisputeTransferStepListener.QUALIFIER)
    public ConcurrentKafkaListenerContainerFactory<String, DisputeTransferStep.Input> disputeTransferStepListenerContainerFactory(
        DisputeTransferStepListener.Settings settings,
        ObjectMapper objectMapper) {

        return KafkaConsumerConfigurer.configure(
            settings, new KafkaConsumerConfigurer.Deserializers<>() {

                @Override
                public Deserializer<String> forKey() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        String.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }

                @Override
                public Deserializer<DisputeTransferStep.Input> forValue() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        DisputeTransferStep.Input.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }
            });
    }

    @Bean
    @Qualifier(DisputeTransferStepPublisher.QUALIFIER)
    public ProducerFactory<String, DisputeTransferStep.Input> disputeTransferStepProducerFactory(
        TransferKafkaConfiguration.ProducerSettings settings,
        ObjectMapper objectMapper) {

        return KafkaProducerConfigurer.configure(
            settings.bootstrapServers(), settings.ack(),
            new KafkaProducerConfigurer.Serializers<>() {

                @Override
                public Serializer<String> forKey() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }

                @Override
                public Serializer<DisputeTransferStep.Input> forValue() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }
            });
    }

    @Bean
    @Qualifier(RollbackReservationStepPublisher.QUALIFIER)
    public KafkaTemplate<String, RollbackReservationStep.Input> rollbackReservationStepKafkaTemplate(
        @Qualifier(RollbackReservationStepPublisher.QUALIFIER)
        ProducerFactory<String, RollbackReservationStep.Input> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean(name = RollbackReservationStepListener.LISTENER_CONTAINER_FACTORY)
    @Qualifier(RollbackReservationStepListener.QUALIFIER)
    public ConcurrentKafkaListenerContainerFactory<String, RollbackReservationStep.Input> rollbackReservationStepListenerContainerFactory(
        RollbackReservationStepListener.Settings settings,
        ObjectMapper objectMapper) {

        return KafkaConsumerConfigurer.configure(
            settings, new KafkaConsumerConfigurer.Deserializers<>() {

                @Override
                public Deserializer<String> forKey() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        String.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }

                @Override
                public Deserializer<RollbackReservationStep.Input> forValue() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        RollbackReservationStep.Input.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }
            });
    }

    @Bean
    @Qualifier(RollbackReservationStepPublisher.QUALIFIER)
    public ProducerFactory<String, RollbackReservationStep.Input> rollbackReservationStepProducerFactory(
        TransferKafkaConfiguration.ProducerSettings settings,
        ObjectMapper objectMapper) {

        return KafkaProducerConfigurer.configure(
            settings.bootstrapServers(), settings.ack(),
            new KafkaProducerConfigurer.Serializers<>() {

                @Override
                public Serializer<String> forKey() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }

                @Override
                public Serializer<RollbackReservationStep.Input> forValue() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }
            });
    }

    public interface RequiredBeans { }

    public interface RequiredSettings {

        AbortTransferStepListener.Settings abortTransferStepListenerSettings();

        CommitTransferStepListener.Settings commitTransferStepListenerSettings();

        DisputeTransferStepListener.Settings disputeTransferStepListenerSettings();

        RollbackReservationStepListener.Settings rollbackReservationStepListenerSettings();

        TransferKafkaConfiguration.ProducerSettings transferProducerSettings();

    }

    public record ProducerSettings(String bootstrapServers, String ack) { }

}
