package io.mojaloop.common.component.redis;

import lombok.Getter;
import org.redisson.api.RedissonClient;

@Getter
public class RedisPubSubClient {

    private final RedissonClient redissonClient;

    public RedisPubSubClient(RedisPubSubConfigurer.SettingsProvider settingsProvider) {

        this.redissonClient = new RedisPubSubConfigurer().configure(settingsProvider);
    }

}
