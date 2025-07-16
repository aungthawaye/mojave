package io.mojaloop.common.component.redis;

import lombok.Getter;
import org.redisson.api.RedissonClient;

@Getter
public class RedissonOpsClient {

    private final RedissonClient redissonClient;

    RedissonOpsClient(RedissonClient redissonClient) {

        assert redissonClient != null;

        this.redissonClient = redissonClient;
    }

}
