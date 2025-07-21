package io.mojaloop.common.component.redis;

import org.springframework.context.annotation.Bean;

public class RedissonOpsClientConfiguration {

    @Bean
    public RedissonOpsClient redissonOpsClient(RedissonOpsClientConfigurer.Settings settings) {

        return RedissonOpsClientConfigurer.configure(settings);
    }

    public interface RequiredSettings {

        RedissonOpsClientConfigurer.Settings redissonOpsClientSettings();

    }

}
