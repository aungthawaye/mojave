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
package org.mojave.core.wallet.intercom;

import org.mojave.component.web.spring.security.AuthenticationErrorWriter;
import org.mojave.component.web.spring.security.Authenticator;
import org.mojave.core.wallet.domain.cache.BalanceCache;
import org.mojave.core.wallet.domain.cache.PositionCache;
import org.mojave.core.wallet.domain.cache.strategy.timer.BalanceTimerCache;
import org.mojave.core.wallet.domain.cache.strategy.timer.PositionTimerCache;
import org.mojave.core.wallet.domain.component.BalanceUpdater;
import org.mojave.core.wallet.domain.component.PositionUpdater;
import org.mojave.core.wallet.domain.component.mysql.MySqlBalanceUpdater;
import org.mojave.core.wallet.domain.component.mysql.MySqlPositionUpdater;
import org.mojave.core.wallet.domain.repository.BalanceRepository;
import org.mojave.core.wallet.domain.repository.PositionRepository;
import org.mojave.core.wallet.intercom.controller.component.EmptyErrorWriter;
import org.mojave.core.wallet.intercom.controller.component.EmptyGatekeeper;
import org.springframework.context.annotation.Bean;

final class WalletIntercomDependencies implements WalletIntercomConfiguration.RequiredDependencies {

    private final BalanceUpdater balanceUpdater;

    private final PositionUpdater positionUpdater;

    private final BalanceCache balanceCache;

    private final PositionCache positionCache;

    public WalletIntercomDependencies(BalanceRepository balanceRepository,
                                      PositionRepository positionRepository) {

        assert balanceRepository != null;
        assert positionRepository != null;

        this.balanceUpdater = new MySqlBalanceUpdater(new MySqlBalanceUpdater.BalanceDbSettings(
            new MySqlBalanceUpdater.BalanceDbSettings.Connection(
                System.getenv("MYSQL_BALANCE_DB_URL"), System.getenv("MYSQL_BALANCE_DB_USER"),
                System.getenv("MYSQL_BALANCE_DB_PASSWORD"),
                Long.parseLong(System.getenv("MYSQL_BALANCE_DB_CONNECTION_TIMEOUT")),
                Long.parseLong(System.getenv("MYSQL_BALANCE_DB_VALIDATION_TIMEOUT")),
                Long.parseLong(System.getenv("MYSQL_BALANCE_DB_MAX_LIFETIME_TIMEOUT")),
                Long.parseLong(System.getenv("MYSQL_BALANCE_DB_IDLE_TIMEOUT")),
                Long.parseLong(System.getenv("MYSQL_BALANCE_DB_KEEPALIVE_TIMEOUT")), false),
            new MySqlBalanceUpdater.BalanceDbSettings.Pool(
                "wallet-balance", Integer.parseInt(System.getenv("MYSQL_BALANCE_DB_MIN_POOL_SIZE")),
                Integer.parseInt(System.getenv("MYSQL_BALANCE_DB_MAX_POOL_SIZE")))));

        this.positionUpdater = new MySqlPositionUpdater(new MySqlPositionUpdater.PositionDbSettings(
            new MySqlPositionUpdater.PositionDbSettings.Connection(
                System.getenv("MYSQL_POSITION_DB_URL"), System.getenv("MYSQL_POSITION_DB_USER"),
                System.getenv("MYSQL_POSITION_DB_PASSWORD"),
                Long.parseLong(System.getenv("MYSQL_POSITION_DB_CONNECTION_TIMEOUT")),
                Long.parseLong(System.getenv("MYSQL_POSITION_DB_VALIDATION_TIMEOUT")),
                Long.parseLong(System.getenv("MYSQL_POSITION_DB_MAX_LIFETIME_TIMEOUT")),
                Long.parseLong(System.getenv("MYSQL_POSITION_DB_IDLE_TIMEOUT")),
                Long.parseLong(System.getenv("MYSQL_POSITION_DB_KEEPALIVE_TIMEOUT")), false),
            new MySqlPositionUpdater.PositionDbSettings.Pool(
                "wallet-position",
                Integer.parseInt(System.getenv("MYSQL_POSITION_DB_MIN_POOL_SIZE")),
                Integer.parseInt(System.getenv("MYSQL_POSITION_DB_MAX_POOL_SIZE")))));

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
    public BalanceCache walletCache() {

        return this.balanceCache;
    }

}
