package io.mojaloop.component.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

public class KafkaProducerConfigurer {

    public static <K, V> ProducerFactory<K, V> configure(String bootstrapServers, String ack, Serializer<K, V> serializer) {

        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.ACKS_CONFIG, ack);

        var factory = new DefaultKafkaProducerFactory<K, V>(props);

        factory.setValueSerializer(serializer.forValue());
        factory.setKeySerializer(serializer.forKey());

        return factory;
    }

    public interface Serializer<K, V> {

        JsonSerializer<K> forKey();

        JsonSerializer<V> forValue();

    }

    public record ProducerSettings(String bootstrapServers, String ack) { }

}
