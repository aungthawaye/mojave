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

package io.mojaloop.core.accounting.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.kafka.KafkaConsumerConfigurer;
import io.mojaloop.core.accounting.consumer.listener.PostLedgerFlowListener;
import io.mojaloop.core.accounting.contract.command.ledger.PostLedgerFlowCommand;
import io.mojaloop.core.accounting.domain.AccountingDomainConfiguration;
import io.mojaloop.core.accounting.domain.cache.AccountCache;
import io.mojaloop.core.accounting.domain.cache.ChartEntryCache;
import io.mojaloop.core.accounting.domain.cache.FlowDefinitionCache;
import io.mojaloop.core.accounting.domain.cache.strategy.timer.AccountTimerCache;
import io.mojaloop.core.accounting.domain.cache.strategy.timer.ChartEntryTimerCache;
import io.mojaloop.core.accounting.domain.cache.strategy.timer.FlowDefinitionTimerCache;
import io.mojaloop.core.accounting.domain.component.ledger.Ledger;
import io.mojaloop.core.accounting.domain.component.ledger.strategy.MySqlLedger;
import io.mojaloop.core.accounting.domain.repository.AccountRepository;
import io.mojaloop.core.accounting.domain.repository.ChartEntryRepository;
import io.mojaloop.core.accounting.domain.repository.FlowDefinitionRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@EnableKafka
@ComponentScan(basePackages = {"io.mojaloop.core.accounting.consumer"})
@Import(value = {AccountingDomainConfiguration.class})
final class AccountingConsumerConfiguration implements AccountingDomainConfiguration.RequiredBeans {

    private final Ledger ledger;

    private final AccountCache accountCache;

    private final ChartEntryCache chartEntryCache;

    private final FlowDefinitionCache flowDefinitionCache;

    public AccountingConsumerConfiguration(AccountRepository accountRepository,
                                           ChartEntryRepository chartEntryRepository,
                                           FlowDefinitionRepository flowDefinitionRepository,
                                           ObjectMapper objectMapper) {

        assert accountRepository != null;
        assert chartEntryRepository != null;
        assert flowDefinitionRepository != null;
        assert objectMapper != null;

        this.ledger = new MySqlLedger(
            new MySqlLedger.LedgerDbSettings(
                new MySqlLedger.LedgerDbSettings.Connection(
                    System.getenv("ACC_LEDGER_DB_URL"),
                    System.getenv("ACC_LEDGER_DB_USER"), System.getenv("ACC_LEDGER_DB_PASSWORD")),
                new MySqlLedger.LedgerDbSettings.Pool(
                    "accounting-ledger",
                    Integer.parseInt(System.getenv("ACC_LEDGER_DB_MIN_POOL_SIZE")),
                    Integer.parseInt(System.getenv("ACC_LEDGER_DB_MAX_POOL_SIZE")))), objectMapper);

        this.accountCache = new AccountTimerCache(
            accountRepository, Integer.parseInt(
            System.getenv().getOrDefault("ACCOUNT_TIMER_CACHE_REFRESH_INTERVAL_MS", "5000")));

        this.chartEntryCache = new ChartEntryTimerCache(
            chartEntryRepository, Integer.parseInt(
            System.getenv().getOrDefault("CHART_ENTRY_TIMER_CACHE_REFRESH_INTERVAL_MS", "5000")));

        this.flowDefinitionCache = new FlowDefinitionTimerCache(
            flowDefinitionRepository,
            Integer.parseInt(System
                                 .getenv()
                                 .getOrDefault(
                                     "FLOW_DEFINITION_TIMER_CACHE_REFRESH_INTERVAL_MS", "5000")));

    }

    @Bean
    @Override
    public AccountCache accountCache() {

        return this.accountCache;
    }

    @Bean(name = PostLedgerFlowListener.LISTENER_CONTAINER_FACTORY)
    @Qualifier(PostLedgerFlowListener.QUALIFIER)
    public ConcurrentKafkaListenerContainerFactory<String, PostLedgerFlowCommand.Input> addStepListenerContainerFactory(
        PostLedgerFlowListener.Settings settings,
        ObjectMapper objectMapper) {

        return KafkaConsumerConfigurer.configure(
            settings, new KafkaConsumerConfigurer.Deserializer<>() {

                @Override
                public JsonDeserializer<String> forKey() {

                    var deserializer = new JsonDeserializer<>(String.class, objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }

                @Override
                public JsonDeserializer<PostLedgerFlowCommand.Input> forValue() {

                    var deserializer = new JsonDeserializer<>(
                        PostLedgerFlowCommand.Input.class, objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }
            });
    }

    @Bean
    @Override
    public ChartEntryCache chartEntryCache() {

        return this.chartEntryCache;
    }

    @Bean
    @Override
    public FlowDefinitionCache flowDefinitionCache() {

        return this.flowDefinitionCache;
    }

    @Bean
    @Override
    public Ledger ledger() {

        return this.ledger;
    }

    public interface RequiredBeans { }

    public interface RequiredSettings extends AccountingDomainConfiguration.RequiredSettings {

        PostLedgerFlowListener.Settings postLedgerFlowListenerSettings();

    }

}
