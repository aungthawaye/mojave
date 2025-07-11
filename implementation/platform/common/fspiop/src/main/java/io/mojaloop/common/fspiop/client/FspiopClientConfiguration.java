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
package io.mojaloop.common.fspiop.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.common.component.ComponentConfiguration;
import io.mojaloop.common.component.retrofit.RetrofitService;
import io.mojaloop.common.component.retrofit.converter.NullOrEmptyConverterFactory;
import io.mojaloop.common.fspiop.service.AccountLookUpService;
import io.mojaloop.common.fspiop.service.QuotingService;
import io.mojaloop.common.fspiop.service.TransferService;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.util.Map;

@Import(value = {ComponentConfiguration.class})
public class FspiopClientConfiguration {

    @Bean
    public AccountLookUpService accountLookUpService(AccountLookUpService.Settings settings, ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(AccountLookUpService.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withConverterFactories(new NullOrEmptyConverterFactory(), ScalarsConverterFactory.create(),
                                           JacksonConverterFactory.create(objectMapper))
                   .build();
    }

    @Bean
    public QuotingService quotingService(QuotingService.Settings settings, ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(QuotingService.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withConverterFactories(new NullOrEmptyConverterFactory(), ScalarsConverterFactory.create(),
                                           JacksonConverterFactory.create(objectMapper))
                   .build();
    }

    @Bean
    public TransferService transferService(TransferService.Settings settings, ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(TransferService.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withConverterFactories(new NullOrEmptyConverterFactory(), ScalarsConverterFactory.create(),
                                           JacksonConverterFactory.create(objectMapper))
                   .build();
    }

    public interface SettingsProvider {

        Settings fspiopClientConfigurationSettings();

    }

    public record Settings(String fspId,
                           String fspName,
                           String ilpSecret,
                           String base64PrivateKey,
                           Map<String, String> base64FspPublicKeys) { }

}
