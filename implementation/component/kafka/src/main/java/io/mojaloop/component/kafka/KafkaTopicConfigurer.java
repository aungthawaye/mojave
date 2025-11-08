package io.mojaloop.component.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

public class KafkaTopicConfigurer {

    public static KafkaAdmin.NewTopics newTopics(KafkaAdmin kafkaAdmin, TopicSettings[] topicSettings) {

        if (topicSettings == null || topicSettings.length == 0) {
            return new KafkaAdmin.NewTopics();
        }

        var newTopics = new NewTopic[topicSettings.length];

        for (int i = 0; i < topicSettings.length; i++) {

            var topicSettingsItem = topicSettings[i];

            if (topicSettingsItem == null) {
                continue;
            }

            newTopics[i] = TopicBuilder.name(topicSettingsItem.name).partitions(topicSettingsItem.partitions).replicas(topicSettingsItem.replicationFactor)
                                       .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(topicSettingsItem.retentionMs))
                                       .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE).build();
        }

        return new KafkaAdmin.NewTopics(newTopics);
    }

    public record TopicSettings(String name, int retentionMs, int partitions, short replicationFactor) { }

}
