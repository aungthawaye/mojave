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
import io.mojaloop.component.jackson.ComponentJacksonConfiguration;
import io.mojaloop.fspiop.common.FspiopCommonConfiguration;
import io.mojaloop.fspiop.common.data.ParticipantDetails;
import io.mojaloop.fspiop.component.retrofit.FspiopErrorDecoder;
import io.mojaloop.fspiop.component.retrofit.FspiopInvocationErrorHandler;
import io.mojaloop.fspiop.component.retrofit.FspiopSigningInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(value = {FspiopCommonConfiguration.class, ComponentJacksonConfiguration.class})
public class FspiopComponentConfiguration {

    @Bean
    public FspiopSigningInterceptor fspSigningInterceptor(ParticipantDetails participantDetails, ObjectMapper objectMapper) {

        return new FspiopSigningInterceptor(participantDetails, objectMapper);
    }

    @Bean
    public FspiopErrorDecoder fspiopErrorDecoder(ObjectMapper objectMapper) {

        return new FspiopErrorDecoder(objectMapper);
    }

    @Bean
    public FspiopInvocationErrorHandler fspiopInvocationErrorHandler() {

        return new FspiopInvocationErrorHandler();
    }

    public interface RequiredSettings extends FspiopCommonConfiguration.RequiredSettings, ComponentJacksonConfiguration.RequiredSettings { }

}
