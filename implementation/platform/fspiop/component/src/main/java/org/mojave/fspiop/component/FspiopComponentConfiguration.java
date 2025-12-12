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
import org.mojave.fspiop.component.participant.ParticipantContext;
import org.mojave.fspiop.component.retrofit.FspiopErrorDecoder;
import org.mojave.fspiop.component.retrofit.FspiopSigningInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import tools.jackson.databind.ObjectMapper;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

@Import(
    value = {
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

    @Bean
    public ParticipantContext participantContext(ParticipantSettings participantSettings)
        throws NoSuchAlgorithmException, InvalidKeySpecException {

        return ParticipantContext.with(
            participantSettings.fspCode(), participantSettings.fspName(),
            participantSettings.ilpSecret(), participantSettings.signJws(),
            participantSettings.verifyJws(), participantSettings.privateKeyPem(),
            participantSettings.fspPublicKeyPem());
    }

    public interface RequiredBeans { }

    public interface RequiredSettings extends MiscConfiguration.RequiredSettings {

        ParticipantSettings participantSettings();

    }

    public record ParticipantSettings(String fspCode,
                                      String fspName,
                                      String ilpSecret,
                                      boolean signJws,
                                      boolean verifyJws,
                                      String privateKeyPem,
                                      Map<String, String> fspPublicKeyPem) { }

}
