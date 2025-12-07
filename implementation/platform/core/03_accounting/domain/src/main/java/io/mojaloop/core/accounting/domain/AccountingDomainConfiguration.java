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

package io.mojaloop.core.accounting.domain;

import io.mojaloop.component.jpa.routing.RoutingJpaConfiguration;
import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.core.accounting.domain.cache.AccountCache;
import io.mojaloop.core.accounting.domain.cache.ChartEntryCache;
import io.mojaloop.core.accounting.domain.cache.FlowDefinitionCache;
import io.mojaloop.core.accounting.domain.component.ledger.Ledger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = {"io.mojaloop.core.accounting.domain"})
@Import(
    value = {
        MiscConfiguration.class,
        AccountingFlywayConfiguration.class,
        RoutingJpaConfiguration.class})
public class AccountingDomainConfiguration {

    public AccountingDomainConfiguration() {

    }

    public interface RequiredBeans {

        AccountCache accountCache();

        ChartEntryCache chartEntryCache();

        FlowDefinitionCache flowDefinitionCache();

        Ledger ledger();

    }

    public interface RequiredSettings extends MiscConfiguration.RequiredSettings,
                                              AccountingFlywayConfiguration.RequiredSettings,
                                              RoutingJpaConfiguration.RequiredSettings {

    }

}
