package io.mojaloop.common.component.redis;

import lombok.Getter;
import org.redisson.api.RedissonClient;

@Getter
public class RedisOpsClient {

    private final RedissonClient redissonClient;

    public RedisOpsClient(RedisOpsConfigurer.SettingsProvider settingsProvider) {

        this.redissonClient = new RedisOpsConfigurer().configure(settingsProvider);
    }

}
