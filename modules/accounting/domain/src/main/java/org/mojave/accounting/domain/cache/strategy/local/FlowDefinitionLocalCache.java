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

package org.mojave.accounting.domain.cache.strategy.local;

import jakarta.annotation.PostConstruct;
import org.mojave.accounting.contract.data.FlowDefinitionData;
import org.mojave.accounting.domain.cache.FlowDefinitionCache;
import org.mojave.accounting.domain.repository.FlowDefinitionRepository;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.accounting.FlowDefinitionId;
import org.mojave.scheme.rule.accounting.scenario.AccountingScenario;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class FlowDefinitionLocalCache implements FlowDefinitionCache {

    private final FlowDefinitionRepository flowDefinitionRepository;

    private final Map<Long, FlowDefinitionData> withId;

    private final Map<String, FlowDefinitionData> withScenarioCurrency;

    public FlowDefinitionLocalCache(final FlowDefinitionRepository flowDefinitionRepository) {

        Objects.requireNonNull(flowDefinitionRepository);

        this.flowDefinitionRepository = flowDefinitionRepository;

        this.withId = new ConcurrentHashMap<>();
        this.withScenarioCurrency = new ConcurrentHashMap<>();
    }

    @Override
    public void clear() {

        this.withId.clear();
        this.withScenarioCurrency.clear();
    }

    @Override
    public void delete(final FlowDefinitionId flowDefinitionId) {

        final var removed = this.withId.remove(flowDefinitionId.getId());

        if (removed != null) {
            final var key = FlowDefinitionCache.Keys.forTransaction(
                removed.scenario(), removed.currency());
            this.withScenarioCurrency.remove(key);
        }
    }

    @Override
    public FlowDefinitionData get(final FlowDefinitionId flowDefinitionId) {

        if (flowDefinitionId == null) {
            return null;
        }

        var data = this.withId.get(flowDefinitionId.getId());

        if (data == null) {

            var entity = this.flowDefinitionRepository.findById(flowDefinitionId).orElse(null);

            if (entity != null) {
                data = entity.convert();
                this.save(data);
            }

            return data;
        }

        return data;
    }

    @Override
    public FlowDefinitionData get(final AccountingScenario scenario, final Currency currency) {

        if (scenario == null || currency == null) {
            return null;
        }

        final var key = FlowDefinitionCache.Keys.forTransaction(scenario, currency);

        var data = this.withScenarioCurrency.get(key);

        if (data == null) {

            var entity = this.flowDefinitionRepository
                             .findOne(FlowDefinitionRepository.Filters
                                          .withScenario(scenario)
                                          .and(FlowDefinitionRepository.Filters.withCurrency(
                                              currency)))
                             .orElse(null);

            if (entity != null) {
                data = entity.convert();
                this.save(data);
            }

            return data;
        }

        return data;
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

        final var key = FlowDefinitionCache.Keys.forTransaction(
            flowDefinition.scenario(), flowDefinition.currency());
        this.withScenarioCurrency.put(key, flowDefinition);
    }

}
