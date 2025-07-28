/*-
 * ================================================================================
 * Mojaloop OSS
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

package io.mojaloop.fspiop.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.component.retrofit.converter.NullOrEmptyConverterFactory;
import io.mojaloop.fspiop.client.component.retrofit.FspiopJwsSigningInterceptor;
import io.mojaloop.fspiop.client.service.LookUpService;
import io.mojaloop.fspiop.client.service.QuotingService;
import io.mojaloop.fspiop.client.service.TransferService;
import io.mojaloop.fspiop.common.FspiopCommonConfiguration;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@ComponentScan(basePackages = {"io.mojaloop.fspiop.client"})
@Import(value = {FspiopCommonConfiguration.class})
public class FspiopClientConfiguration {

    @Bean
    public LookUpService lookUpService(LookUpService.Settings settings,
                                       FspiopJwsSigningInterceptor fspiopJwsSigningInterceptor,
                                       ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(LookUpService.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withInterceptors(fspiopJwsSigningInterceptor)
                   .withConverterFactories(new NullOrEmptyConverterFactory(),
                                           ScalarsConverterFactory.create(),
                                           JacksonConverterFactory.create(objectMapper))
                   .build();

    }

    @Bean
    public QuotingService quotingService(QuotingService.Settings settings,
                                         FspiopJwsSigningInterceptor fspiopJwsSigningInterceptor,
                                         ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(QuotingService.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withInterceptors(fspiopJwsSigningInterceptor)
                   .withConverterFactories(new NullOrEmptyConverterFactory(),
                                           ScalarsConverterFactory.create(),
                                           JacksonConverterFactory.create(objectMapper))
                   .build();
    }

    @Bean
    public TransferService transferService(TransferService.Settings settings,
                                           FspiopJwsSigningInterceptor fspiopJwsSigningInterceptor,
                                           ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(TransferService.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withInterceptors(fspiopJwsSigningInterceptor)
                   .withConverterFactories(new NullOrEmptyConverterFactory(),
                                           ScalarsConverterFactory.create(),
                                           JacksonConverterFactory.create(objectMapper))
                   .build();
    }

    public interface RequiredSettings extends FspiopCommonConfiguration.RequiredSettings {

        LookUpService.Settings lookUpServiceSettings();

        QuotingService.Settings quotingServiceSettings();

        TransferService.Settings transferServiceSettings();

    }

}
