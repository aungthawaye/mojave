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

package io.mojaloop.core.accounting.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.jpa.routing.RoutingJpaConfiguration;
import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.component.redis.RedissonOpsClientConfiguration;
import io.mojaloop.core.accounting.domain.component.ledger.Ledger;
import io.mojaloop.core.accounting.domain.component.ledger.strategy.MySqlLedger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = {"io.mojaloop.core.accounting.domain"})
@Import(value = {MiscConfiguration.class, RoutingJpaConfiguration.class, RedissonOpsClientConfiguration.class})
public class AccountDomainConfiguration {

    @Bean
    public Ledger ledgers(MySqlLedger.LedgerDbSettings ledgerDbSettings, ObjectMapper objectMapper) {

        return new MySqlLedger(ledgerDbSettings, objectMapper);
    }

    public interface RequiredBeans { }

    public interface RequiredSettings extends MiscConfiguration.RequiredSettings,
                                              RoutingJpaConfiguration.RequiredSettings,
                                              RedissonOpsClientConfiguration.RequiredSettings {

        MySqlLedger.LedgerDbSettings ledgerDbSettings();

    }

}
