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

package io.mojaloop.fspiop.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.fspiop.common.FspiopCommonConfiguration;
import io.mojaloop.fspiop.common.participant.ParticipantContext;
import io.mojaloop.fspiop.component.retrofit.FspiopErrorDecoder;
import io.mojaloop.fspiop.component.retrofit.FspiopInvocationExceptionHandler;
import io.mojaloop.fspiop.component.retrofit.FspiopSigningInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(value = {FspiopCommonConfiguration.class, MiscConfiguration.class})
public class FspiopComponentConfiguration {

    @Bean
    public FspiopSigningInterceptor fspSigningInterceptor(ParticipantContext participantContext, ObjectMapper objectMapper) {

        return new FspiopSigningInterceptor(participantContext, objectMapper);
    }

    @Bean
    public FspiopErrorDecoder fspiopErrorDecoder(ObjectMapper objectMapper) {

        return new FspiopErrorDecoder(objectMapper);
    }

    @Bean
    public FspiopInvocationExceptionHandler fspiopInvocationErrorHandler() {

        return new FspiopInvocationExceptionHandler();
    }

    public interface RequiredBeans extends FspiopCommonConfiguration.RequiredBeans, MiscConfiguration.RequiredBeans { }

    public interface RequiredSettings extends FspiopCommonConfiguration.RequiredSettings, MiscConfiguration.RequiredSettings { }

}
