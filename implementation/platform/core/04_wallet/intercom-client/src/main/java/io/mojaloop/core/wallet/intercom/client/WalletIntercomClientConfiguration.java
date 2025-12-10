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
/*-
 * ==============================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==============================================================================
 */

package io.mojaloop.core.wallet.intercom.client;

import tools.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.wallet.intercom.client.service.WalletIntercomService;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import(value = {MiscConfiguration.class})
@ComponentScan(basePackages = {"io.mojaloop.core.wallet.intercom.client"})
public class WalletIntercomClientConfiguration implements MiscConfiguration.RequiredBeans {

    @Bean
    public WalletIntercomService.PositionCommand positionCommands(WalletIntercomService.Settings settings,
                                                                  ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(WalletIntercomService.PositionCommand.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withDefaultFactories(objectMapper)
                   .build();
    }

    @Bean
    public WalletIntercomService.PositionQuery positionQuery(WalletIntercomService.Settings settings,
                                                             ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(WalletIntercomService.PositionQuery.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withDefaultFactories(objectMapper)
                   .build();
    }

    @Bean
    public WalletIntercomService.BalanceCommand walletCommands(WalletIntercomService.Settings settings,
                                                               ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(WalletIntercomService.BalanceCommand.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withDefaultFactories(objectMapper)
                   .build();
    }

    @Bean
    public WalletIntercomService.BalanceQuery walletQuery(WalletIntercomService.Settings settings,
                                                          ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(WalletIntercomService.BalanceQuery.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withDefaultFactories(objectMapper)
                   .build();
    }

    public interface RequiredBeans extends MiscConfiguration.RequiredBeans {

    }

    public interface RequiredSettings extends MiscConfiguration.RequiredSettings {

        WalletIntercomService.Settings walletIntercomServiceSettings();

    }

}
