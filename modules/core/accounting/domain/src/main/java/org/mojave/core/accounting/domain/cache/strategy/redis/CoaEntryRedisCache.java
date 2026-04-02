/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */

package org.mojave.core.accounting.domain.cache.strategy.redis;

import jakarta.annotation.PostConstruct;
import org.mojave.common.datatype.identifier.accounting.CoaEntryId;
import org.mojave.common.datatype.identifier.accounting.CoaId;
import org.mojave.common.datatype.type.accounting.CoaEntryCode;
import org.mojave.component.redis.RedissonOpsClient;
import org.mojave.core.accounting.contract.data.CoaEntryData;
import org.mojave.core.accounting.domain.cache.CoaEntryCache;
import org.mojave.core.accounting.domain.repository.CoaEntryRepository;
import org.redisson.api.RMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CoaEntryRedisCache implements CoaEntryCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoaEntryRedisCache.class);

    private final CoaEntryRepository coaEntryRepository;

    private final RMap<Long, CoaEntryData> withId;

    private final RMap<String, CoaEntryData> withCode;

    public CoaEntryRedisCache(CoaEntryRepository coaEntryRepository,
                              RedissonOpsClient redissonOpsClient) {

        Objects.requireNonNull(coaEntryRepository);
        Objects.requireNonNull(redissonOpsClient);

        this.coaEntryRepository = coaEntryRepository;

        this.withId = redissonOpsClient.getRedissonClient().getMap(Names.WITH_ID);
        this.withCode = redissonOpsClient.getRedissonClient().getMap(Names.WITH_CODE);
    }

    @Override
    public void clear() {

        this.withId.clear();
        this.withCode.clear();
    }

    @Override
    public void delete(CoaEntryId coaEntryId) {

        var removed = this.withId.remove(coaEntryId.getId());

        if (removed != null) {
            this.withCode.remove(removed.code().value());
        }
    }

    @Override
    public CoaEntryData get(CoaEntryId coaEntryId) {

        return this.withId.get(coaEntryId.getId());
    }

    @Override
    public CoaEntryData get(CoaEntryCode code) {

        return this.withCode.get(code.value());
    }

    @Override
    public Set<CoaEntryData> get(CoaId coaId) {

        var result = new HashSet<CoaEntryData>();

        for (var entry : this.withId.values()) {
            if (entry.coaId().equals(coaId)) {
                result.add(entry);
            }
        }

        return result;
    }

    @PostConstruct
    public void postConstruct() {

        this.clear();

        var entries = this.coaEntryRepository.findAll();

        for (var entry : entries) {
            this.save(entry.convert());
        }
    }

    @Override
    public void save(CoaEntryData coaEntry) {

        this.withId.put(coaEntry.coaEntryId().getId(), coaEntry);
        this.withCode.put(coaEntry.code().value(), coaEntry);
    }

}
