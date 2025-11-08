package io.mojaloop.component.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;

public class KafkaConsumerConfigurer {

    public static <K, V> ConcurrentKafkaListenerContainerFactory<K, V> configure(ConsumerSettings opts, Deserializer<K, V> deserializer) {

        var props = new HashMap<String, Object>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, opts.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, opts.getGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, opts.getAutoOffsetReset());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.toString(opts.isAutoCommit()));
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, opts.getClientId());

        var consumerFactory = new DefaultKafkaConsumerFactory<>(props, deserializer.forKey(), deserializer.forValue());

        var listenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<K, V>();

        listenerContainerFactory.setConsumerFactory(consumerFactory);

        if (opts.getConcurrency() > 0) {
            listenerContainerFactory.setConcurrency(opts.getConcurrency());
        }

        if (opts.getPollTimeoutMs() > 0) {
            listenerContainerFactory.getContainerProperties().setPollTimeout(opts.getPollTimeoutMs());
        }

        // Ack mode is only meaningful when auto-commit is false
        if (!opts.isAutoCommit() && opts.getAckMode() != null) {
            listenerContainerFactory.getContainerProperties().setAckMode(opts.getAckMode());
        }

        return listenerContainerFactory;
    }

    public interface Deserializer<K, V> {

        JsonDeserializer<K> forKey();

        JsonDeserializer<V> forValue();

    }

    @Getter
    @AllArgsConstructor
    public static class ConsumerSettings {

        private String bootstrapServers;

        private String groupId;

        private String clientId;

        private String autoOffsetReset;

        private int concurrency;

        private int pollTimeoutMs;

        private boolean autoCommit;

        private ContainerProperties.AckMode ackMode;

    }

}
