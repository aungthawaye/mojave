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
package io.mojaloop.core.wallet.admin.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.wallet.admin.client.service.WalletAdminService;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import(value = {MiscConfiguration.class})
@ComponentScan(basePackages = {"io.mojaloop.core.wallet.admin.client"})
public class WalletAdminClientConfiguration {

    @Bean
    public WalletAdminService.WalletCommand walletCommands(WalletAdminService.Settings settings, ObjectMapper objectMapper) {

        return RetrofitService.newBuilder(WalletAdminService.WalletCommand.class, settings.baseUrl())
                              .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                              .withDefaultFactories(objectMapper)
                              .build();
    }

    @Bean
    public WalletAdminService.WalletQuery walletQuery(WalletAdminService.Settings settings, ObjectMapper objectMapper) {

        return RetrofitService.newBuilder(WalletAdminService.WalletQuery.class, settings.baseUrl())
                              .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                              .withDefaultFactories(objectMapper)
                              .build();
    }

    @Bean
    public WalletAdminService.PositionCommand positionCommands(WalletAdminService.Settings settings, ObjectMapper objectMapper) {

        return RetrofitService.newBuilder(WalletAdminService.PositionCommand.class, settings.baseUrl())
                              .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                              .withDefaultFactories(objectMapper)
                              .build();
    }

    @Bean
    public WalletAdminService.PositionQuery positionQuery(WalletAdminService.Settings settings, ObjectMapper objectMapper) {

        return RetrofitService.newBuilder(WalletAdminService.PositionQuery.class, settings.baseUrl())
                              .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                              .withDefaultFactories(objectMapper)
                              .build();
    }

    public interface RequiredBeans extends MiscConfiguration.RequiredBeans { }

    public interface RequiredSettings extends MiscConfiguration.RequiredSettings {

        WalletAdminService.Settings walletAdminServiceSettings();

    }
}
