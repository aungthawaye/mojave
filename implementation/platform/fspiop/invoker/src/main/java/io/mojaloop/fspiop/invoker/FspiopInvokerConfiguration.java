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

package io.mojaloop.fspiop.invoker;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.component.retrofit.converter.NullOrEmptyConverterFactory;
import io.mojaloop.fspiop.component.FspiopComponentConfiguration;
import io.mojaloop.fspiop.component.retrofit.FspiopSigningInterceptor;
import io.mojaloop.fspiop.invoker.api.PartiesService;
import io.mojaloop.fspiop.invoker.api.QuotesService;
import io.mojaloop.fspiop.invoker.api.TransfersService;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@ComponentScan(basePackages = {"io.mojaloop.fspiop.invoker"})
@Import(value = {FspiopComponentConfiguration.class})
public class FspiopInvokerConfiguration implements FspiopComponentConfiguration.RequiredBeans {

    @Bean
    public PartiesService partiesService(PartiesService.Settings settings,
                                         FspiopSigningInterceptor fspiopSigningInterceptor,
                                         ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(PartiesService.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withInterceptors(fspiopSigningInterceptor)
                   .withConverterFactories(new NullOrEmptyConverterFactory(),
                                           ScalarsConverterFactory.create(),
                                           JacksonConverterFactory.create(objectMapper))
                   .build();

    }

    @Bean
    public QuotesService quotesService(QuotesService.Settings settings,
                                       FspiopSigningInterceptor fspiopSigningInterceptor,
                                       ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(QuotesService.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withInterceptors(fspiopSigningInterceptor)
                   .withConverterFactories(new NullOrEmptyConverterFactory(),
                                           ScalarsConverterFactory.create(),
                                           JacksonConverterFactory.create(objectMapper))
                   .build();
    }

    @Bean
    public TransfersService transfersService(TransfersService.Settings settings,
                                             FspiopSigningInterceptor fspiopSigningInterceptor,
                                             ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(TransfersService.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withInterceptors(fspiopSigningInterceptor)
                   .withConverterFactories(new NullOrEmptyConverterFactory(),
                                           ScalarsConverterFactory.create(),
                                           JacksonConverterFactory.create(objectMapper))
                   .build();
    }

    public interface RequiredBeans { }

    public interface RequiredSettings extends FspiopComponentConfiguration.RequiredSettings {

        PartiesService.Settings partiesServiceSettings();

        QuotesService.Settings quotesServiceSettings();

        TransfersService.Settings transfersServiceSettings();

    }

}
