/*-
 * ================================================================================
 * Mojave
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

package io.mojaloop.core.accounting.domain.cache.strategy.redis;

import io.mojaloop.component.redis.RedissonOpsClient;
import io.mojaloop.core.accounting.contract.data.FlowDefinitionData;
import io.mojaloop.core.accounting.domain.cache.FlowDefinitionCache;
import io.mojaloop.core.accounting.domain.repository.FlowDefinitionRepository;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.accounting.FlowDefinitionId;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlowDefinitionRedisCache implements FlowDefinitionCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowDefinitionRedisCache.class);

    private final FlowDefinitionRepository flowDefinitionRepository;

    private final RMap<Long, FlowDefinitionData> withId;

    private final RMap<String, FlowDefinitionData> withTxnTypeCurrency;

    public FlowDefinitionRedisCache(final FlowDefinitionRepository flowDefinitionRepository, final RedissonOpsClient redissonOpsClient) {

        assert flowDefinitionRepository != null;
        assert redissonOpsClient != null;

        this.flowDefinitionRepository = flowDefinitionRepository;

        this.withId = redissonOpsClient.getRedissonClient().getMap(Names.WITH_ID);
        this.withTxnTypeCurrency = redissonOpsClient.getRedissonClient().getMap(Names.WITH_TXN_TYPE_CURRENCY);
    }

    @Override
    public void clear() {

        this.withId.clear();
        this.withTxnTypeCurrency.clear();
    }

    @Override
    public void delete(final FlowDefinitionId flowDefinitionId) {

        final var removed = this.withId.remove(flowDefinitionId.getId());

        if (removed != null) {
            final var key = FlowDefinitionCache.Keys.forTransaction(removed.transactionType(), removed.currency());
            this.withTxnTypeCurrency.remove(key);
        }
    }

    @Override
    public FlowDefinitionData get(final FlowDefinitionId flowDefinitionId) {

        return this.withId.get(flowDefinitionId.getId());
    }

    @Override
    public FlowDefinitionData get(final TransactionType transactionType, final Currency currency) {

        final var key = FlowDefinitionCache.Keys.forTransaction(transactionType, currency);
        return this.withTxnTypeCurrency.get(key);
    }

    @PostConstruct
    public void postConstruct() {

        this.clear();

        final var definitions = this.flowDefinitionRepository.findAll();

        for (final var def : definitions) {
            this.save(def.convert());
        }
    }

    @Override
    public void save(final FlowDefinitionData flowDefinition) {

        this.withId.put(flowDefinition.flowDefinitionId().getId(), flowDefinition);

        final var key = FlowDefinitionCache.Keys.forTransaction(flowDefinition.transactionType(), flowDefinition.currency());
        this.withTxnTypeCurrency.put(key, flowDefinition);
    }

}
