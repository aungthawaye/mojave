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

import io.mojaloop.component.jpa.routing.RoutingJpaConfiguration;
import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.core.wallet.domain.component.BalanceUpdater;
import io.mojaloop.core.wallet.domain.component.PositionUpdater;
import io.mojaloop.core.wallet.domain.component.mysql.MySqlBalanceUpdater;
import io.mojaloop.core.wallet.domain.component.mysql.MySqlPositionUpdater;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = {"io.mojaloop.core.wallet.domain"})
@Import(value = {MiscConfiguration.class, RoutingJpaConfiguration.class})
public class WalletDomainConfiguration {

    @Bean
    public BalanceUpdater balanceUpdater(MySqlBalanceUpdater.BalanceDbSettings balanceDbSettings) {

        return new MySqlBalanceUpdater(balanceDbSettings);
    }

    @Bean
    public PositionUpdater positionUpdater(MySqlBalanceUpdater.BalanceDbSettings balanceDbSettings) {

        return new MySqlPositionUpdater(balanceDbSettings);
    }

    public interface RequiredBeans { }

    public interface RequiredSettings
        extends MiscConfiguration.RequiredSettings, RoutingJpaConfiguration.RequiredSettings {

        MySqlBalanceUpdater.BalanceDbSettings balanceDbSettings();

    }

}
