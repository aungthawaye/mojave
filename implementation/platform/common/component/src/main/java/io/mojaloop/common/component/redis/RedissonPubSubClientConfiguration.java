package io.mojaloop.common.component.redis;

import org.springframework.context.annotation.Bean;

public class RedissonPubSubClientConfiguration {

    @Bean
    public RedissonPubSubClient redissonPubSubClient(RedissonPubSubClientConfigurer.Settings settings) {

        return RedissonPubSubClientConfigurer.configure(settings);
    }

    public interface RequiredSettings {

        RedissonPubSubClientConfigurer.Settings redissonPubSubClientSettings();

    }

}
