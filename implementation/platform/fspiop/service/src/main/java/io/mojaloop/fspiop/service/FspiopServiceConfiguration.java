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
package io.mojaloop.fspiop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.component.retrofit.converter.NullOrEmptyConverterFactory;
import io.mojaloop.fspiop.common.FspiopCommonConfiguration;
import io.mojaloop.fspiop.component.FspiopComponentConfiguration;
import io.mojaloop.fspiop.component.retrofit.FspiopSigningInterceptor;
import io.mojaloop.fspiop.service.api.PartiesResponseService;
import io.mojaloop.fspiop.service.api.QuotesResponseService;
import io.mojaloop.fspiop.service.api.TransfersResponseService;
import io.mojaloop.fspiop.service.component.FspiopSpringSecurityConfiguration;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@ComponentScan(basePackages = {"io.mojaloop.fspiop.service"})
@Import(value = {
    FspiopSpringSecurityConfiguration.class, FspiopCommonConfiguration.class, FspiopComponentConfiguration.class})
public class FspiopServiceConfiguration {

    @Bean
    public RetrofitService.ForwardingService forwardingService(ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(RetrofitService.ForwardingService.class, "https://2ne1.com")
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withConverterFactories(new NullOrEmptyConverterFactory(),
                                           ScalarsConverterFactory.create(),
                                           JacksonConverterFactory.create(objectMapper))
                   .build();
    }

    @Bean
    public PartiesResponseService partiesResponseService(FspiopSigningInterceptor fspiopSigningInterceptor) {

        return RetrofitService
                   .newBuilder(PartiesResponseService.class, "https://2ne1.com")
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withInterceptors(fspiopSigningInterceptor)
                   .withConverterFactories(new NullOrEmptyConverterFactory(),
                                           ScalarsConverterFactory.create(),
                                           JacksonConverterFactory.create())
                   .build();
    }

    @Bean
    public QuotesResponseService quotesResponseService(FspiopSigningInterceptor fspiopSigningInterceptor) {

        return RetrofitService
                   .newBuilder(QuotesResponseService.class, "https://2ne1.com")
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withInterceptors(fspiopSigningInterceptor)
                   .withConverterFactories(new NullOrEmptyConverterFactory(),
                                           ScalarsConverterFactory.create(),
                                           JacksonConverterFactory.create())
                   .build();
    }

    @Bean
    public TransfersResponseService transfersResponseService(FspiopSigningInterceptor fspiopSigningInterceptor) {

        return RetrofitService
                   .newBuilder(TransfersResponseService.class, "https://2ne1.com")
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withInterceptors(fspiopSigningInterceptor)
                   .withConverterFactories(new NullOrEmptyConverterFactory(),
                                           ScalarsConverterFactory.create(),
                                           JacksonConverterFactory.create())
                   .build();
    }

    public interface RequiredSettings extends FspiopSpringSecurityConfiguration.RequiredSettings,
                                              FspiopCommonConfiguration.RequiredSettings,
                                              FspiopComponentConfiguration.RequiredSettings {

    }

}
