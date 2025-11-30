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
import io.mojaloop.component.web.logging.RequestIdMdcConfiguration;
import io.mojaloop.component.web.spring.mvc.WebMvcExtension;
import io.mojaloop.component.web.spring.security.AuthenticationErrorWriter;
import io.mojaloop.component.web.spring.security.Authenticator;
import io.mojaloop.component.web.spring.security.SpringSecurityConfiguration;
import io.mojaloop.component.web.spring.security.SpringSecurityConfigurer;
import io.mojaloop.core.common.datatype.DatatypeConfiguration;
import io.mojaloop.core.wallet.domain.WalletDomainConfiguration;
import io.mojaloop.core.wallet.domain.cache.PositionCache;
import io.mojaloop.core.wallet.domain.cache.BalanceCache;
import io.mojaloop.core.wallet.domain.cache.strategy.timer.BalanceTimerCache;
import io.mojaloop.core.wallet.domain.cache.strategy.timer.PositionTimerCache;
import io.mojaloop.core.wallet.domain.component.BalanceUpdater;
import io.mojaloop.core.wallet.domain.component.PositionUpdater;
import io.mojaloop.core.wallet.domain.component.mysql.MySqlBalanceUpdater;
import io.mojaloop.core.wallet.domain.component.mysql.MySqlPositionUpdater;
import io.mojaloop.core.wallet.domain.repository.BalanceRepository;
import io.mojaloop.core.wallet.domain.repository.PositionRepository;
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
@Import(
    value = {
        OpenApiConfiguration.class,
        DatatypeConfiguration.class,
        RequestIdMdcConfiguration.class,
        WalletDomainConfiguration.class,
        RestErrorConfiguration.class,
        SpringSecurityConfiguration.class})
final class WalletIntercomConfiguration extends WebMvcExtension
    implements WalletDomainConfiguration.RequiredBeans,
               SpringSecurityConfiguration.RequiredBeans,
               SpringSecurityConfiguration.RequiredSettings {

    private final BalanceUpdater balanceUpdater;

    private final PositionUpdater positionUpdater;

    private final BalanceCache balanceCache;

    private final PositionCache positionCache;

    public WalletIntercomConfiguration(ObjectMapper objectMapper,
                                       BalanceRepository balanceRepository,
                                       PositionRepository positionRepository) {

        super(objectMapper);

        this.balanceUpdater = new MySqlBalanceUpdater(new MySqlBalanceUpdater.BalanceDbSettings(
            new MySqlBalanceUpdater.BalanceDbSettings.Connection(
                System.getenv("WLT_MYSQL_BALANCE_DB_URL"),
                System.getenv("WLT_MYSQL_BALANCE_DB_USER"),
                System.getenv("WLT_MYSQL_BALANCE_DB_PASSWORD")),
            new MySqlBalanceUpdater.BalanceDbSettings.Pool(
                "wallet-balance",
                Integer.parseInt(System.getenv("WLT_MYSQL_BALANCE_DB_MIN_POOL_SIZE")),
                Integer.parseInt(System.getenv("WLT_MYSQL_BALANCE_DB_MAX_POOL_SIZE")))));

        this.positionUpdater = new MySqlPositionUpdater(new MySqlPositionUpdater.PositionDbSettings(
            new MySqlPositionUpdater.PositionDbSettings.Connection(
                System.getenv("WLT_MYSQL_POSITION_DB_URL"),
                System.getenv("WLT_MYSQL_POSITION_DB_USER"),
                System.getenv("WLT_MYSQL_POSITION_DB_PASSWORD")),
            new MySqlPositionUpdater.PositionDbSettings.Pool(
                "wallet-position",
                Integer.parseInt(System.getenv("WLT_MYSQL_POSITION_DB_MIN_POOL_SIZE")),
                Integer.parseInt(System.getenv("WLT_MYSQL_POSITION_DB_MAX_POOL_SIZE")))));

        this.balanceCache = new BalanceTimerCache(
            balanceRepository, Integer.parseInt(
            System.getenv().getOrDefault("WALLET_TIMER_CACHE_REFRESH_INTERVAL_MS", "5000")));

        this.positionCache = new PositionTimerCache(
            positionRepository, Integer.parseInt(
            System.getenv().getOrDefault("POSITION_TIMER_CACHE_REFRESH_INTERVAL_MS", "5000")));
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
    public BalanceCache walletCache() {

        return this.balanceCache;
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer(
        TomcatSettings settings) {

        return factory -> factory.setPort(settings.portNo());
    }

    public interface RequiredSettings
        extends WalletDomainConfiguration.RequiredSettings, OpenApiConfiguration.RequiredSettings {

        TomcatSettings tomcatSettings();

    }

    public record TomcatSettings(int portNo) { }

}
