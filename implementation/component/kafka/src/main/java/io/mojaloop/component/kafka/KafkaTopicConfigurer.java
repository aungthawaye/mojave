/*-
 * ================================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */

package io.mojaloop.component.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

public class KafkaTopicConfigurer {

    public static KafkaAdmin.NewTopics newTopics(KafkaAdmin kafkaAdmin,
                                                 TopicSettings[] topicSettings) {

        if (topicSettings == null || topicSettings.length == 0) {
            return new KafkaAdmin.NewTopics();
        }

        var newTopics = new NewTopic[topicSettings.length];

        for (int i = 0; i < topicSettings.length; i++) {

            var topicSettingsItem = topicSettings[i];

            if (topicSettingsItem == null) {
                continue;
            }

            newTopics[i] = TopicBuilder
                               .name(topicSettingsItem.name)
                               .partitions(topicSettingsItem.partitions)
                               .replicas(topicSettingsItem.replicationFactor)
                               .config(
                                   TopicConfig.RETENTION_MS_CONFIG,
                                   String.valueOf(topicSettingsItem.retentionMs))
                               .config(
                                   TopicConfig.CLEANUP_POLICY_CONFIG,
                                   TopicConfig.CLEANUP_POLICY_DELETE)
                               .build();
        }

        return new KafkaAdmin.NewTopics(newTopics);
    }

    public record TopicSettings(String name,
                                int retentionMs,
                                int partitions,
                                short replicationFactor) { }

}
