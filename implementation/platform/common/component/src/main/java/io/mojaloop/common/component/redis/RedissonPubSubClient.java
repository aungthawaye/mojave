package io.mojaloop.common.component.redis;

import lombok.Getter;
import org.redisson.api.RedissonClient;

@Getter
public class RedissonPubSubClient {

    private final RedissonClient redissonClient;

    RedissonPubSubClient(RedissonClient redissonClient) {

        assert redissonClient != null;

        this.redissonClient = redissonClient;
    }

}
