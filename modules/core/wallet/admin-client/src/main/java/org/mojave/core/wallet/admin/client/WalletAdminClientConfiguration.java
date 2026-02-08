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

package org.mojave.core.wallet.admin.client;

import okhttp3.logging.HttpLoggingInterceptor;
import org.mojave.component.misc.MiscConfiguration;
import org.mojave.component.retrofit.RetrofitService;
import org.mojave.core.wallet.admin.client.service.WalletAdminService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import tools.jackson.databind.ObjectMapper;

@Import(value = {MiscConfiguration.class})
@ComponentScan(basePackages = {"org.mojave.core.wallet.admin.client"})
public class WalletAdminClientConfiguration {

    @Bean
    public WalletAdminService.PositionCommand positionCommands(WalletAdminService.Settings settings,
                                                               ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(WalletAdminService.PositionCommand.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withDefaultFactories(objectMapper)
                   .build();
    }

    @Bean
    public WalletAdminService.PositionQuery positionQuery(WalletAdminService.Settings settings,
                                                          ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(WalletAdminService.PositionQuery.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withDefaultFactories(objectMapper)
                   .build();
    }

    @Bean
    public WalletAdminService.BalanceCommand walletCommands(WalletAdminService.Settings settings,
                                                            ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(WalletAdminService.BalanceCommand.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withDefaultFactories(objectMapper)
                   .build();
    }

    @Bean
    public WalletAdminService.BalanceQuery walletQuery(WalletAdminService.Settings settings,
                                                       ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(WalletAdminService.BalanceQuery.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withDefaultFactories(objectMapper)
                   .build();
    }

    public interface RequiredDependencies extends MiscConfiguration.RequiredDependencies { }

    public interface RequiredSettings extends MiscConfiguration.RequiredSettings {

        WalletAdminService.Settings walletAdminServiceSettings();

    }

}
