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
package org.mojave.rail.fspiop.bootstrap;

import okhttp3.logging.HttpLoggingInterceptor;
import org.mojave.component.retrofit.RetrofitService;
import org.mojave.component.web.spring.security.AuthenticationErrorWriter;
import org.mojave.component.web.spring.security.Authenticator;
import org.mojave.component.web.spring.security.SpringSecurityConfiguration;
import org.mojave.component.web.spring.security.SpringSecurityConfigurer;
import org.mojave.rail.fspiop.component.participant.ParticipantContext;
import org.mojave.rail.fspiop.component.FspiopComponentConfiguration;
import org.mojave.rail.fspiop.component.retrofit.FspiopSigningInterceptor;
import org.mojave.rail.fspiop.bootstrap.api.PartiesResponseService;
import org.mojave.rail.fspiop.bootstrap.api.QuotesResponseService;
import org.mojave.rail.fspiop.bootstrap.api.TransfersResponseService;
import org.mojave.rail.fspiop.bootstrap.component.FspiopServiceErrorWriter;
import org.mojave.rail.fspiop.bootstrap.component.FspiopServiceGatekeeper;
import org.mojave.rail.fspiop.bootstrap.component.ParticipantVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import tools.jackson.databind.ObjectMapper;
import java.util.Objects;

@ComponentScan(basePackages = {"org.mojave.rail.fspiop.bootstrap"})
@Import(
    value = {
        FspiopComponentConfiguration.class,
        SpringSecurityConfiguration.class})
public class FspiopServiceConfiguration implements SpringSecurityConfiguration.RequiredBeans {

    private final SpringSecurityConfigurer.Settings springSecuritySettings;

    private final ParticipantContext participantContext;

    private final ParticipantVerifier participantVerifier;

    private final ObjectMapper objectMapper;

    private final FspiopServiceConfiguration.ServiceSettings serviceSettings;

    public FspiopServiceConfiguration(SpringSecurityConfigurer.Settings springSecuritySettings,
                                      ParticipantContext participantContext,
                                      ParticipantVerifier participantVerifier,
                                      ObjectMapper objectMapper,
                                      FspiopServiceConfiguration.ServiceSettings serviceSettings) {

        Objects.requireNonNull(springSecuritySettings);
        Objects.requireNonNull(participantContext);
        Objects.requireNonNull(participantVerifier);
        Objects.requireNonNull(objectMapper);
        Objects.requireNonNull(serviceSettings);

        this.springSecuritySettings = springSecuritySettings;
        this.participantContext = participantContext;
        this.participantVerifier = participantVerifier;
        this.objectMapper = objectMapper;
        this.serviceSettings = serviceSettings;

    }

    @Bean
    @Override
    public AuthenticationErrorWriter authenticationErrorWriter() {

        return new FspiopServiceErrorWriter(this.objectMapper);
    }

    @Bean
    @Override
    public Authenticator authenticator() {

        return new FspiopServiceGatekeeper(
            this.springSecuritySettings, this.participantContext,
            this.participantVerifier, this.objectMapper, this.serviceSettings);
    }

    @Bean
    public RetrofitService.ForwardingService forwardingService(ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(RetrofitService.ForwardingService.class, "https://2ne1.com")
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withDefaultFactories(objectMapper)
                   .build();
    }

    @Bean
    public PartiesResponseService partiesResponseService(FspiopSigningInterceptor fspiopSigningInterceptor) {

        return RetrofitService
                   .newBuilder(PartiesResponseService.class, "https://2ne1.com")
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withInterceptors(fspiopSigningInterceptor)
                   .withDefaultFactories(this.objectMapper)
                   .build();
    }

    @Bean
    public QuotesResponseService quotesResponseService(FspiopSigningInterceptor fspiopSigningInterceptor) {

        return RetrofitService
                   .newBuilder(QuotesResponseService.class, "https://2ne1.com")
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withInterceptors(fspiopSigningInterceptor)
                   .withDefaultFactories(this.objectMapper)
                   .build();
    }

    @Bean
    public TransfersResponseService transfersResponseService(FspiopSigningInterceptor fspiopSigningInterceptor) {

        return RetrofitService
                   .newBuilder(TransfersResponseService.class, "https://2ne1.com")
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withInterceptors(fspiopSigningInterceptor)
                   .withDefaultFactories(this.objectMapper)
                   .build();
    }

    public interface RequiredBeans {

        ParticipantVerifier participantVerifier();

    }

    public interface RequiredSettings extends SpringSecurityConfiguration.RequiredSettings,
                                              FspiopComponentConfiguration.RequiredSettings {

        ServiceSettings serviceSettings();

    }

    public record ServiceSettings(int requestAgeMs, boolean requestAgeVerification) { }

}
