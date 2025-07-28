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
package io.mojaloop.common.fspiop;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.common.component.ComponentConfiguration;
import io.mojaloop.common.fspiop.component.retrofit.FspiopErrorDecoder;
import io.mojaloop.common.fspiop.component.retrofit.FspiopJwsSigningInterceptor;
import io.mojaloop.common.fspiop.support.ParticipantDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

@Import(value = {ComponentConfiguration.class})
public class FspiopConfiguration {

    @Bean
    public FspiopErrorDecoder fspiopErrorDecoder(ObjectMapper objectMapper) {

        return new FspiopErrorDecoder(objectMapper);
    }

    @Bean
    public FspiopJwsSigningInterceptor fspiopSignatureInterceptor(ParticipantDetails participantDetails, ObjectMapper objectMapper) {

        return new FspiopJwsSigningInterceptor(participantDetails, objectMapper);
    }

    @Bean
    public ParticipantDetails participantSettings(FspiopConfiguration.Settings settings)
        throws NoSuchAlgorithmException, InvalidKeySpecException {

        return ParticipantDetails.with(settings.fspCode(),
                                       settings.fspName(),
                                       settings.ilpSecret(),
                                       settings.signJws(),
                                       settings.verifyJws(),
                                       settings.base64PrivateKey(),
                                       settings.base64FspPublicKeys());
    }

    public interface RequiredBeans { }

    public interface RequiredSettings extends ComponentConfiguration.RequiredSettings {

        Settings fspiopConfigurationSettings();

    }

    public record Settings(String fspCode,
                           String fspName,
                           String ilpSecret,
                           boolean signJws,
                           boolean verifyJws,
                           String base64PrivateKey,
                           Map<String, String> base64FspPublicKeys) { }

}
