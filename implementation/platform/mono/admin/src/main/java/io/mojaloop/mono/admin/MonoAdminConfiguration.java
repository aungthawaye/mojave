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

package io.mojaloop.mono.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.openapi.OpenApiConfiguration;
import io.mojaloop.component.web.spring.mvc.WebMvcExtension;
import io.mojaloop.component.web.spring.security.SpringSecurityConfiguration;
import io.mojaloop.core.accounting.admin.AccountingAdminConfiguration;
import io.mojaloop.core.accounting.domain.AccountingDomainConfiguration;
import io.mojaloop.core.accounting.domain.component.ledger.strategy.MySqlLedger;
import io.mojaloop.core.participant.admin.ParticipantAdminConfiguration;
import io.mojaloop.core.participant.domain.ParticipantDomainConfiguration;
import io.mojaloop.core.wallet.admin.WalletAdminConfiguration;
import io.mojaloop.core.wallet.domain.WalletDomainConfiguration;
import io.mojaloop.core.wallet.domain.component.mysql.MySqlBalanceUpdater;
import io.mojaloop.core.wallet.domain.component.mysql.MySqlPositionUpdater;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@EnableAsync
@ComponentScan(
    basePackages = {"io.mojaloop.mono.admin.controller"})
@Import(
    value = {
        OpenApiConfiguration.class,
        SpringSecurityConfiguration.class,
        ParticipantAdminConfiguration.class,
        AccountingAdminConfiguration.class,
        WalletAdminConfiguration.class})
public class MonoAdminConfiguration extends WebMvcExtension {

    public MonoAdminConfiguration(ObjectMapper objectMapper) {

        super(objectMapper);
    }

    public interface RequiredDependencies extends OpenApiConfiguration.RequiredBeans,
                                                  SpringSecurityConfiguration.RequiredBeans,
                                                  ParticipantAdminConfiguration.RequiredDependencies,
                                                  AccountingAdminConfiguration.RequiredDependencies,
                                                  WalletAdminConfiguration.RequiredDependencies { }

    public interface RequiredSettings extends OpenApiConfiguration.RequiredSettings,
                                              SpringSecurityConfiguration.RequiredSettings,
                                              ParticipantDomainConfiguration.RequiredSettings,
                                              AccountingDomainConfiguration.RequiredSettings,
                                              WalletDomainConfiguration.RequiredSettings {

        MySqlBalanceUpdater.BalanceDbSettings balanceDbSettings();

        MySqlLedger.LedgerDbSettings ledgerDbSettings();

        MySqlPositionUpdater.PositionDbSettings positionDbSettings();

        TomcatSettings tomcatSettings();

    }

    public record TomcatSettings(int portNo) { }

}
