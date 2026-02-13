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

package org.mojave.core.participant.admin.client;

import okhttp3.logging.HttpLoggingInterceptor;
import org.mojave.component.misc.MiscConfiguration;
import org.mojave.component.retrofit.RetrofitService;
import org.mojave.core.participant.admin.client.service.ParticipantAdminService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import tools.jackson.databind.ObjectMapper;

@Import(value = {MiscConfiguration.class})
@ComponentScan(basePackages = {"org.mojave.core.participant.admin.client"})
public class ParticipantAdminClientConfiguration {

    @Bean
    public ParticipantAdminService.FspCommand fspCommand(ParticipantAdminService.Settings settings,
                                                         ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(ParticipantAdminService.FspCommand.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withDefaultFactories(objectMapper)
                   .build();
    }

    @Bean
    public ParticipantAdminService.FspQuery fspQuery(ParticipantAdminService.Settings settings,
                                                     ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(ParticipantAdminService.FspQuery.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withDefaultFactories(objectMapper)
                   .build();
    }

    @Bean
    public ParticipantAdminService.FspGroupQuery fspGroupQuery(
        ParticipantAdminService.Settings settings, ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(ParticipantAdminService.FspGroupQuery.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withDefaultFactories(objectMapper)
                   .build();
    }

    @Bean
    public ParticipantAdminService.HubCommands hubCommands(ParticipantAdminService.Settings settings,
                                                           ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(ParticipantAdminService.HubCommands.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withDefaultFactories(objectMapper)
                   .build();
    }

    @Bean
    public ParticipantAdminService.HubQuery hubQuery(ParticipantAdminService.Settings settings,
                                                     ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(ParticipantAdminService.HubQuery.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withDefaultFactories(objectMapper)
                   .build();
    }

    @Bean
    public ParticipantAdminService.OracleCommands oracleCommands(ParticipantAdminService.Settings settings,
                                                                 ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(ParticipantAdminService.OracleCommands.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withDefaultFactories(objectMapper)
                   .build();
    }

    @Bean
    public ParticipantAdminService.OracleQuery oracleQuery(ParticipantAdminService.Settings settings,
                                                           ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(ParticipantAdminService.OracleQuery.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withDefaultFactories(objectMapper)
                   .build();
    }

    public interface RequiredDependencies extends MiscConfiguration.RequiredDependencies { }

    public interface RequiredSettings extends MiscConfiguration.RequiredSettings {

        ParticipantAdminService.Settings participantAdminServiceSettings();

    }

}
