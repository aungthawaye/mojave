package io.mojaloop.core.participant.store.cache.redis;

import io.mojaloop.core.participant.store.cache.ParticipantStore;
import io.mojaloop.core.participant.store.qualifier.Qualifiers;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ParticipantRedisStore implements ParticipantStore {

    private final RedissonClient redissonClient;

    public ParticipantRedisStore(@Qualifier(Qualifiers.CENTRAL_CACHE_OPS) RedissonClient redissonClient) {

        assert redissonClient != null;

        this.redissonClient = redissonClient;
    }

}
