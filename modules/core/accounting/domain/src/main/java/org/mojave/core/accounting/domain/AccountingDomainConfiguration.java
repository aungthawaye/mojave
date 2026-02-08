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

import org.mojave.component.jpa.routing.RoutingJpaConfiguration;
import org.mojave.component.misc.MiscConfiguration;
import org.mojave.core.accounting.domain.cache.AccountCache;
import org.mojave.core.accounting.domain.cache.ChartEntryCache;
import org.mojave.core.accounting.domain.cache.FlowDefinitionCache;
import org.mojave.provider.ledger.contract.Ledger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = {"org.mojave.core.accounting.domain"})
@Import(
    value = {
        MiscConfiguration.class,
        RoutingJpaConfiguration.class})
public class AccountingDomainConfiguration {

    public AccountingDomainConfiguration() {

    }

    public interface RequiredDependencies {

        AccountCache accountCache();

        ChartEntryCache chartEntryCache();

        FlowDefinitionCache flowDefinitionCache();

        Ledger ledger();

    }

    public interface RequiredSettings
        extends MiscConfiguration.RequiredSettings, RoutingJpaConfiguration.RequiredSettings {

    }

}
