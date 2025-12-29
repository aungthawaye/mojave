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
package org.mojave.rail.fspiop.invoker;

import okhttp3.logging.HttpLoggingInterceptor;
import org.mojave.component.retrofit.RetrofitService;
import org.mojave.component.retrofit.converter.Jackson3ConverterFactory;
import org.mojave.component.retrofit.converter.NullOrEmptyConverterFactory;
import org.mojave.rail.fspiop.component.FspiopComponentConfiguration;
import org.mojave.rail.fspiop.component.retrofit.FspiopSigningInterceptor;
import org.mojave.rail.fspiop.invoker.api.PartiesService;
import org.mojave.rail.fspiop.invoker.api.QuotesService;
import org.mojave.rail.fspiop.invoker.api.TransfersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.util.Base64;

@ComponentScan(basePackages = {"org.mojave.fspiop.invoker"})
@Import(value = {FspiopComponentConfiguration.class})
public class FspiopInvokerConfiguration implements FspiopComponentConfiguration.RequiredBeans {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspiopInvokerConfiguration.class);

    @Bean
    public PartiesService partiesService(TransportSettings transportSettings,
                                         PartiesService.Settings settings,
                                         FspiopSigningInterceptor fspiopSigningInterceptor,
                                         ObjectMapper objectMapper) {

        var builder = RetrofitService
                          .newBuilder(PartiesService.class, settings.baseUrl())
                          .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                          .withInterceptors(fspiopSigningInterceptor)
                          .withConverterFactories(
                              new NullOrEmptyConverterFactory(), ScalarsConverterFactory.create(),
                              Jackson3ConverterFactory.create(objectMapper));

        if (transportSettings.useMutualTls()) {

            LOGGER.info("(PartiesService) Using mutual TLS for communication with FSPs.");

            var keyStoreSettings = transportSettings.keyStoreSettings;
            var trustStoreSettings = transportSettings.trustStoreSettings;

            var keyStoreBase64 = Base64.getMimeDecoder().decode(keyStoreSettings.p12b64Content);
            var trustStoreBase64 = Base64.getMimeDecoder().decode(trustStoreSettings.p12b64Content);

            try (var keyStoreInput = new ByteArrayInputStream(keyStoreBase64);
                 var trustStoreInput = new ByteArrayInputStream(trustStoreBase64)) {

                builder.withMutualTLS(
                    keyStoreInput, transportSettings.keyStoreSettings.password, trustStoreInput,
                    transportSettings.trustStoreSettings.password,
                    transportSettings.ignoreHostnameVerification);

            } catch (Exception e) {
                throw new RuntimeException("Failed to read key store.", e);
            }
        }

        return builder.build();

    }

    @Bean
    public QuotesService quotesService(TransportSettings transportSettings,
                                       QuotesService.Settings settings,
                                       FspiopSigningInterceptor fspiopSigningInterceptor,
                                       ObjectMapper objectMapper) {

        var builder = RetrofitService
                          .newBuilder(QuotesService.class, settings.baseUrl())
                          .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                          .withInterceptors(fspiopSigningInterceptor)
                          .withConverterFactories(
                              new NullOrEmptyConverterFactory(), ScalarsConverterFactory.create(),
                              Jackson3ConverterFactory.create(objectMapper));

        if (transportSettings.useMutualTls()) {

            LOGGER.info("(QuotesService) Using mutual TLS for communication with FSPs.");

            var keyStoreSettings = transportSettings.keyStoreSettings;
            var trustStoreSettings = transportSettings.trustStoreSettings;

            var keyStoreBase64 = Base64.getMimeDecoder().decode(keyStoreSettings.p12b64Content);
            var trustStoreBase64 = Base64.getMimeDecoder().decode(trustStoreSettings.p12b64Content);

            try (var keyStoreInput = new ByteArrayInputStream(keyStoreBase64);
                 var trustStoreInput = new ByteArrayInputStream(trustStoreBase64)) {

                builder.withMutualTLS(
                    keyStoreInput, transportSettings.keyStoreSettings.password, trustStoreInput,
                    transportSettings.trustStoreSettings.password,
                    transportSettings.ignoreHostnameVerification);

            } catch (Exception e) {
                throw new RuntimeException("Failed to read key store.", e);
            }
        }

        return builder.build();
    }

    @Bean
    public TransfersService transfersService(TransportSettings transportSettings,
                                             TransfersService.Settings settings,
                                             FspiopSigningInterceptor fspiopSigningInterceptor,
                                             ObjectMapper objectMapper) {

        var builder = RetrofitService
                          .newBuilder(TransfersService.class, settings.baseUrl())
                          .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                          .withInterceptors(fspiopSigningInterceptor)
                          .withConverterFactories(
                              new NullOrEmptyConverterFactory(), ScalarsConverterFactory.create(),
                              Jackson3ConverterFactory.create(objectMapper));

        if (transportSettings.useMutualTls()) {

            LOGGER.info("(TransfersService) Using mutual TLS for communication with FSPs.");

            var keyStoreSettings = transportSettings.keyStoreSettings;
            var trustStoreSettings = transportSettings.trustStoreSettings;

            var keyStoreBase64 = Base64.getMimeDecoder().decode(keyStoreSettings.p12b64Content);
            var trustStoreBase64 = Base64.getMimeDecoder().decode(trustStoreSettings.p12b64Content);

            try (var keyStoreInput = new ByteArrayInputStream(keyStoreBase64);
                 var trustStoreInput = new ByteArrayInputStream(trustStoreBase64)) {

                builder.withMutualTLS(
                    keyStoreInput, transportSettings.keyStoreSettings.password, trustStoreInput,
                    transportSettings.trustStoreSettings.password,
                    transportSettings.ignoreHostnameVerification);

            } catch (Exception e) {
                throw new RuntimeException("Failed to read key store.", e);
            }
        }

        return builder.build();
    }

    public interface RequiredBeans { }

    public interface RequiredSettings extends FspiopComponentConfiguration.RequiredSettings {

        TransportSettings fspiopInvokerTransportSettings();

        PartiesService.Settings partiesServiceSettings();

        QuotesService.Settings quotesServiceSettings();

        TransfersService.Settings transfersServiceSettings();

    }

    public record TransportSettings(boolean useMutualTls,
                                    KeyStoreSettings keyStoreSettings,
                                    TrustStoreSettings trustStoreSettings,
                                    boolean ignoreHostnameVerification) {

        public record KeyStoreSettings(String p12b64Content, String password) { }

        public record TrustStoreSettings(String p12b64Content, String password) { }

    }

}
