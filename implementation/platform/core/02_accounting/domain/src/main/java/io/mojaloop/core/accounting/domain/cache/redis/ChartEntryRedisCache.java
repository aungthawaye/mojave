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

package io.mojaloop.core.accounting.domain.cache.redis;

import io.mojaloop.component.redis.RedissonOpsClient;
import io.mojaloop.core.accounting.contract.data.ChartEntryData;
import io.mojaloop.core.accounting.domain.cache.ChartEntryCache;
import io.mojaloop.core.accounting.domain.repository.ChartEntryRepository;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartId;
import io.mojaloop.core.common.datatype.type.accounting.ChartEntryCode;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class ChartEntryRedisCache implements ChartEntryCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChartEntryRedisCache.class);

    private final ChartEntryRepository chartEntryRepository;

    private final RMap<Long, ChartEntryData> withId;

    private final RMap<String, ChartEntryData> withCode;

    public ChartEntryRedisCache(ChartEntryRepository chartEntryRepository, RedissonOpsClient redissonOpsClient) {

        assert chartEntryRepository != null;
        assert redissonOpsClient != null;

        this.chartEntryRepository = chartEntryRepository;

        this.withId = redissonOpsClient.getRedissonClient().getMap(Names.WITH_ID);
        this.withCode = redissonOpsClient.getRedissonClient().getMap(Names.WITH_CODE);
    }

    @Override
    public void clear() {

        this.withId.clear();
        this.withCode.clear();
    }

    @Override
    public void delete(ChartEntryId chartEntryId) {

        var removed = this.withId.remove(chartEntryId.getId());

        if (removed != null) {
            this.withCode.remove(removed.code().value());
        }
    }

    @Override
    public ChartEntryData get(ChartEntryId chartEntryId) {

        return this.withId.get(chartEntryId.getId());
    }

    @Override
    public ChartEntryData get(ChartEntryCode code) {

        return this.withCode.get(code.value());
    }

    @Override
    public Set<ChartEntryData> get(ChartId chartId) {

        var result = new HashSet<ChartEntryData>();

        for (var entry : this.withId.values()) {
            if (entry.chartId().equals(chartId)) {
                result.add(entry);
            }
        }

        return result;
    }

    @PostConstruct
    public void postConstruct() {

        this.clear();

        var entries = this.chartEntryRepository.findAll();

        for (var entry : entries) {
            this.save(entry.convert());
        }
    }

    @Override
    public void save(ChartEntryData chartEntry) {

        this.withId.put(chartEntry.chartEntryId().getId(), chartEntry);
        this.withCode.put(chartEntry.code().value(), chartEntry);
    }

}
