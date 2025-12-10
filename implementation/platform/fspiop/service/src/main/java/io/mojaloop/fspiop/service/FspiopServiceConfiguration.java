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

package io.mojaloop.fspiop.service;

import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.component.web.spring.security.AuthenticationErrorWriter;
import io.mojaloop.component.web.spring.security.Authenticator;
import io.mojaloop.component.web.spring.security.SpringSecurityConfiguration;
import io.mojaloop.component.web.spring.security.SpringSecurityConfigurer;
import io.mojaloop.fspiop.common.participant.ParticipantContext;
import io.mojaloop.fspiop.component.FspiopComponentConfiguration;
import io.mojaloop.fspiop.component.retrofit.FspiopSigningInterceptor;
import io.mojaloop.fspiop.service.api.PartiesResponseService;
import io.mojaloop.fspiop.service.api.QuotesResponseService;
import io.mojaloop.fspiop.service.api.TransfersResponseService;
import io.mojaloop.fspiop.service.component.FspiopServiceErrorWriter;
import io.mojaloop.fspiop.service.component.FspiopServiceGatekeeper;
import io.mojaloop.fspiop.service.component.ParticipantVerifier;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import tools.jackson.databind.ObjectMapper;

@ComponentScan(basePackages = {"io.mojaloop.fspiop.service"})
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

        assert springSecuritySettings != null;
        assert participantContext != null;
        assert participantVerifier != null;
        assert objectMapper != null;
        assert serviceSettings != null;

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
