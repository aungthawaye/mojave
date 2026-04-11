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

package org.mojave.core.accounting.domain;

import org.mojave.core.accounting.contract.engine.LedgerEngine;
import org.mojave.core.accounting.domain.cache.AccountCache;
import org.mojave.core.accounting.domain.cache.CoaEntryCache;
import org.mojave.core.accounting.domain.cache.FlowDefinitionCache;
import org.mojave.core.accounting.domain.cache.strategy.local.AccountLocalCache;
import org.mojave.core.accounting.domain.cache.strategy.local.CoaEntryLocalCache;
import org.mojave.core.accounting.domain.cache.strategy.local.FlowDefinitionLocalCache;
import org.mojave.core.accounting.domain.repository.AccountRepository;
import org.mojave.core.accounting.domain.repository.CoaEntryRepository;
import org.mojave.core.accounting.domain.repository.FlowDefinitionRepository;
import org.mojave.component.jpa.routing.RoutingJpaConfiguration;
import org.mojave.component.misc.MiscConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.util.Objects;

@ComponentScan(basePackages = {"org.mojave.core.accounting.domain"})
@Import(
    value = {
        MiscConfiguration.class,
        RoutingJpaConfiguration.class})
public class AccountingDomainConfiguration {

    private final AccountRepository accountRepository;

    private final CoaEntryRepository coaEntryRepository;

    private final FlowDefinitionRepository flowDefinitionRepository;

    public AccountingDomainConfiguration(final AccountRepository accountRepository,
                                         final CoaEntryRepository coaEntryRepository,
                                         final FlowDefinitionRepository flowDefinitionRepository) {

        Objects.requireNonNull(accountRepository);
        Objects.requireNonNull(coaEntryRepository);
        Objects.requireNonNull(flowDefinitionRepository);

        this.accountRepository = accountRepository;
        this.coaEntryRepository = coaEntryRepository;
        this.flowDefinitionRepository = flowDefinitionRepository;
    }

    @Bean
    public AccountCache accountCache() {

        return new AccountLocalCache(this.accountRepository);
    }

    @Bean
    public CoaEntryCache coaEntryCache() {

        return new CoaEntryLocalCache(this.coaEntryRepository);
    }

    @Bean
    public FlowDefinitionCache flowDefinitionCache() {

        return new FlowDefinitionLocalCache(this.flowDefinitionRepository);
    }

    public interface RequiredDependencies {

        LedgerEngine ledgerEngine();

    }

    public interface RequiredSettings
        extends MiscConfiguration.RequiredSettings, RoutingJpaConfiguration.RequiredSettings {

    }

}
