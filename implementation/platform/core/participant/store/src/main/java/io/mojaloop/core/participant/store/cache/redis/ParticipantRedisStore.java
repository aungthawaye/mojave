/*-
 * ================================================================================
 * Mojaloop OSS
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
