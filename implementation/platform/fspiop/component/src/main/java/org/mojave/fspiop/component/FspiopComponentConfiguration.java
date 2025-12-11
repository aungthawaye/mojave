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
package org.mojave.fspiop.component;

import org.mojave.component.misc.MiscConfiguration;
import org.mojave.fspiop.common.FspiopCommonConfiguration;
import org.mojave.fspiop.common.participant.ParticipantContext;
import org.mojave.fspiop.component.retrofit.FspiopErrorDecoder;
import org.mojave.fspiop.component.retrofit.FspiopSigningInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import tools.jackson.databind.ObjectMapper;

@Import(
    value = {
        FspiopCommonConfiguration.class,
        MiscConfiguration.class})
public class FspiopComponentConfiguration {

    @Bean
    public FspiopSigningInterceptor fspSigningInterceptor(ParticipantContext participantContext,
                                                          ObjectMapper objectMapper) {

        return new FspiopSigningInterceptor(participantContext, objectMapper);
    }

    @Bean
    public FspiopErrorDecoder fspiopErrorDecoder(ObjectMapper objectMapper) {

        return new FspiopErrorDecoder(objectMapper);
    }

    public interface RequiredBeans
        extends FspiopCommonConfiguration.RequiredBeans, MiscConfiguration.RequiredBeans { }

    public interface RequiredSettings
        extends FspiopCommonConfiguration.RequiredSettings, MiscConfiguration.RequiredSettings { }

}
