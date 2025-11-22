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

package io.mojaloop.core.accounting.admin.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.accounting.admin.client.service.AccountingAdminService;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import(value = {MiscConfiguration.class})
@ComponentScan(basePackages = {"io.mojaloop.core.accounting.admin.client"})
public class AccountingAdminClientConfiguration {

    @Bean
    public AccountingAdminService.AccountCommand accountCommands(AccountingAdminService.Settings settings, ObjectMapper objectMapper) {

        return RetrofitService.newBuilder(AccountingAdminService.AccountCommand.class, settings.baseUrl())
                              .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                              .withDefaultFactories(objectMapper)
                              .build();
    }

    @Bean
    public AccountingAdminService.AccountQuery accountQuery(AccountingAdminService.Settings settings, ObjectMapper objectMapper) {

        return RetrofitService.newBuilder(AccountingAdminService.AccountQuery.class, settings.baseUrl())
                              .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                              .withDefaultFactories(objectMapper)
                              .build();
    }

    @Bean
    public AccountingAdminService.ChartCommand chartCommands(AccountingAdminService.Settings settings, ObjectMapper objectMapper) {

        return RetrofitService.newBuilder(AccountingAdminService.ChartCommand.class, settings.baseUrl())
                              .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                              .withDefaultFactories(objectMapper)
                              .build();
    }

    @Bean
    public AccountingAdminService.ChartQuery chartQuery(AccountingAdminService.Settings settings, ObjectMapper objectMapper) {

        return RetrofitService.newBuilder(AccountingAdminService.ChartQuery.class, settings.baseUrl())
                              .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                              .withDefaultFactories(objectMapper)
                              .build();
    }

    @Bean
    public AccountingAdminService.DefinitionCommand definitionCommands(AccountingAdminService.Settings settings, ObjectMapper objectMapper) {

        return RetrofitService.newBuilder(AccountingAdminService.DefinitionCommand.class, settings.baseUrl())
                              .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                              .withDefaultFactories(objectMapper)
                              .build();
    }

    @Bean
    public AccountingAdminService.DefinitionQuery definitionQuery(AccountingAdminService.Settings settings, ObjectMapper objectMapper) {

        return RetrofitService.newBuilder(AccountingAdminService.DefinitionQuery.class, settings.baseUrl())
                              .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                              .withDefaultFactories(objectMapper)
                              .build();
    }

    @Bean
    public AccountingAdminService.LedgerCommand ledgerCommands(AccountingAdminService.Settings settings, ObjectMapper objectMapper) {

        return RetrofitService.newBuilder(AccountingAdminService.LedgerCommand.class, settings.baseUrl())
                              .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                              .withDefaultFactories(objectMapper)
                              .build();
    }

    public interface RequiredBeans extends MiscConfiguration.RequiredBeans { }

    public interface RequiredSettings extends MiscConfiguration.RequiredSettings {

        AccountingAdminService.Settings accountCommandServiceSettings();

    }

}
