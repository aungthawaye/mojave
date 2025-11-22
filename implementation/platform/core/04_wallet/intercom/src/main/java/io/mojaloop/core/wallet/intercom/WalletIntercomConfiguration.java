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

package io.mojaloop.core.wallet.intercom;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.openapi.OpenApiConfiguration;
import io.mojaloop.component.web.error.RestErrorConfiguration;
import io.mojaloop.component.web.spring.mvc.JacksonWebMvcExtension;
import io.mojaloop.component.web.spring.security.AuthenticationErrorWriter;
import io.mojaloop.component.web.spring.security.Authenticator;
import io.mojaloop.component.web.spring.security.SpringSecurityConfiguration;
import io.mojaloop.component.web.spring.security.SpringSecurityConfigurer;
import io.mojaloop.core.wallet.domain.WalletDomainConfiguration;
import io.mojaloop.core.wallet.domain.cache.PositionCache;
import io.mojaloop.core.wallet.domain.cache.WalletCache;
import io.mojaloop.core.wallet.domain.cache.strategy.timer.PositionTimerCache;
import io.mojaloop.core.wallet.domain.cache.strategy.timer.WalletTimerCache;
import io.mojaloop.core.wallet.domain.component.BalanceUpdater;
import io.mojaloop.core.wallet.domain.component.PositionUpdater;
import io.mojaloop.core.wallet.domain.component.mysql.MySqlBalanceUpdater;
import io.mojaloop.core.wallet.domain.component.mysql.MySqlPositionUpdater;
import io.mojaloop.core.wallet.domain.repository.PositionRepository;
import io.mojaloop.core.wallet.domain.repository.WalletRepository;
import io.mojaloop.core.wallet.intercom.controller.component.EmptyErrorWriter;
import io.mojaloop.core.wallet.intercom.controller.component.EmptyGatekeeper;
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
@ComponentScan(basePackages = "io.mojaloop.core.wallet.intercom.controller")
@Import(value = {OpenApiConfiguration.class, WalletDomainConfiguration.class, RestErrorConfiguration.class, SpringSecurityConfiguration.class})
final class WalletIntercomConfiguration extends JacksonWebMvcExtension
    implements WalletDomainConfiguration.RequiredBeans, SpringSecurityConfiguration.RequiredBeans, SpringSecurityConfiguration.RequiredSettings {

    private final BalanceUpdater balanceUpdater;

    private final PositionUpdater positionUpdater;

    private final WalletCache walletCache;

    private final PositionCache positionCache;

    public WalletIntercomConfiguration(ObjectMapper objectMapper,
                                       MySqlBalanceUpdater.BalanceDbSettings balanceDbSettings,
                                       MySqlPositionUpdater.PositionDbSettings positionDbSettings,
                                       WalletRepository walletRepository,
                                       PositionRepository positionRepository) {

        super(objectMapper);
        this.balanceUpdater = new MySqlBalanceUpdater(balanceDbSettings);
        this.positionUpdater = new MySqlPositionUpdater(positionDbSettings);

        this.walletCache = new WalletTimerCache(walletRepository, Integer.parseInt(System.getenv().getOrDefault("WALLET_TIMER_CACHE_REFRESH_INTERVAL_MS", "5000")));
        this.positionCache = new PositionTimerCache(positionRepository, Integer.parseInt(System.getenv().getOrDefault("POSITION_TIMER_CACHE_REFRESH_INTERVAL_MS", "5000")));
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
    public BalanceUpdater balanceUpdater() {

        return this.balanceUpdater;
    }

    @Bean
    @Override
    public PositionCache positionCache() {

        return this.positionCache;
    }

    @Bean
    @Override
    public PositionUpdater positionUpdater() {

        return this.positionUpdater;
    }

    @Bean
    @Override
    public SpringSecurityConfigurer.Settings springSecuritySettings() {

        return new SpringSecurityConfigurer.Settings(null);
    }

    @Bean
    @Override
    public WalletCache walletCache() {

        return this.walletCache;
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer(TomcatSettings settings) {

        return factory -> factory.setPort(settings.portNo());
    }

    public interface RequiredSettings extends WalletDomainConfiguration.RequiredSettings, OpenApiConfiguration.RequiredSettings {

        TomcatSettings tomcatSettings();

    }

    public record TomcatSettings(int portNo) { }

}
