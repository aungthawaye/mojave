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

package io.mojaloop.core.accounting.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.openapi.OpenApiConfiguration;
import io.mojaloop.component.web.error.RestErrorConfiguration;
import io.mojaloop.component.web.logging.RequestIdMdcConfiguration;
import io.mojaloop.component.web.spring.mvc.WebMvcExtension;
import io.mojaloop.component.web.spring.security.AuthenticationErrorWriter;
import io.mojaloop.component.web.spring.security.Authenticator;
import io.mojaloop.component.web.spring.security.SpringSecurityConfiguration;
import io.mojaloop.component.web.spring.security.SpringSecurityConfigurer;
import io.mojaloop.core.accounting.admin.controller.component.EmptyErrorWriter;
import io.mojaloop.core.accounting.admin.controller.component.EmptyGatekeeper;
import io.mojaloop.core.accounting.domain.AccountingDomainConfiguration;
import io.mojaloop.core.accounting.domain.cache.AccountCache;
import io.mojaloop.core.accounting.domain.cache.ChartEntryCache;
import io.mojaloop.core.accounting.domain.cache.FlowDefinitionCache;
import io.mojaloop.core.accounting.domain.cache.strategy.local.AccountLocalCache;
import io.mojaloop.core.accounting.domain.cache.strategy.local.ChartEntryLocalCache;
import io.mojaloop.core.accounting.domain.cache.strategy.local.FlowDefinitionLocalCache;
import io.mojaloop.core.accounting.domain.component.ledger.Ledger;
import io.mojaloop.core.accounting.domain.component.ledger.strategy.EmptyLedger;
import io.mojaloop.core.accounting.domain.component.resolver.AccountResolver;
import io.mojaloop.core.accounting.domain.component.resolver.strategy.CacheBasedAccountResolver;
import io.mojaloop.core.accounting.domain.repository.AccountRepository;
import io.mojaloop.core.accounting.domain.repository.ChartEntryRepository;
import io.mojaloop.core.accounting.domain.repository.FlowDefinitionRepository;
import io.mojaloop.core.common.datatype.DatatypeConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableAutoConfiguration

@EnableWebMvc
@EnableAsync
@ComponentScan(basePackages = "io.mojaloop.core.accounting.admin.controller")
@Import(
    value = {
        OpenApiConfiguration.class,
        DatatypeConfiguration.class,
        RequestIdMdcConfiguration.class,
        AccountingDomainConfiguration.class,
        RestErrorConfiguration.class,
        SpringSecurityConfiguration.class,})
final class AccountingAdminConfiguration extends WebMvcExtension implements
                                                                 AccountingDomainConfiguration.RequiredBeans,
                                                                 SpringSecurityConfiguration.RequiredBeans,
                                                                 SpringSecurityConfiguration.RequiredSettings {

    private final AccountCache accountCache;

    private final AccountResolver accountResolver;

    private final ChartEntryCache chartEntryCache;

    private final FlowDefinitionCache flowDefinitionCache;

    public AccountingAdminConfiguration(ObjectMapper objectMapper,
                                        AccountRepository accountRepository,
                                        ChartEntryRepository chartEntryRepository,
                                        FlowDefinitionRepository flowDefinitionRepository) {

        super(objectMapper);

        assert accountRepository != null;
        assert chartEntryRepository != null;
        assert flowDefinitionRepository != null;

        this.accountCache = new AccountLocalCache(accountRepository);
        this.chartEntryCache = new ChartEntryLocalCache(chartEntryRepository);
        this.flowDefinitionCache = new FlowDefinitionLocalCache(flowDefinitionRepository);

        this.accountResolver = new CacheBasedAccountResolver(this.accountCache);
    }

    @Bean
    @Override
    public AccountCache accountCache() {

        return this.accountCache;
    }

    @Bean
    @Override
    public AccountResolver accountResolver() {

        return this.accountResolver;
    }

    @Bean
    @Override
    public AuthenticationErrorWriter authenticationErrorWriter() {

        return new EmptyErrorWriter();
    }

    @Bean
    @Override
    public Authenticator authenticator() {

        return new EmptyGatekeeper();
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

        return new EmptyLedger();
    }

    @Bean
    @Override
    public SpringSecurityConfigurer.Settings springSecuritySettings() {

        return new SpringSecurityConfigurer.Settings(null);
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer(
        TomcatSettings settings) {

        return factory -> factory.setPort(settings.portNo());
    }

    public interface RequiredSettings extends AccountingDomainConfiguration.RequiredSettings,
                                              OpenApiConfiguration.RequiredSettings {

        TomcatSettings tomcatSettings();

    }

    public record TomcatSettings(int portNo) { }

}
