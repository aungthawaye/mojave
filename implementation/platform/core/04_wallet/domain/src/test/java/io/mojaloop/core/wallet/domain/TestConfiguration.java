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

package io.mojaloop.core.wallet.domain;

import io.mojaloop.component.flyway.FlywayMigration;
import io.mojaloop.core.wallet.domain.cache.PositionCache;
import io.mojaloop.core.wallet.domain.cache.WalletCache;
import io.mojaloop.core.wallet.domain.component.BalanceUpdater;
import io.mojaloop.core.wallet.domain.component.PositionUpdater;
import io.mojaloop.core.wallet.domain.component.mysql.MySqlBalanceUpdater;
import io.mojaloop.core.wallet.domain.component.mysql.MySqlPositionUpdater;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(value = {WalletDomainConfiguration.class, TestSettings.class})
public class TestConfiguration implements WalletDomainConfiguration.RequiredBeans {

    static {

        var flywaySettings = new FlywayMigration.Settings(
            "jdbc:mysql://localhost:3306/ml_wallet?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true", "root", "password",
            "classpath:migration/wallet");

        FlywayMigration.migrate(flywaySettings);
    }

    private final BalanceUpdater balanceUpdater;

    private final PositionUpdater positionUpdater;

    public TestConfiguration(MySqlBalanceUpdater.BalanceDbSettings balanceDbSettings, MySqlPositionUpdater.PositionDbSettings positionDbSettings) {

        this.balanceUpdater = new MySqlBalanceUpdater(balanceDbSettings);
        this.positionUpdater = new MySqlPositionUpdater(positionDbSettings);
    }

    @Bean
    @Override
    public BalanceUpdater balanceUpdater() {

        return this.balanceUpdater;
    }

    @Bean
    @Override
    public PositionUpdater positionUpdater() {

        return this.positionUpdater;
    }

    @Override
    public WalletCache walletCache() {

        return null;
    }

    @Override
    public PositionCache positionCache() {

        return null;
    }

}
