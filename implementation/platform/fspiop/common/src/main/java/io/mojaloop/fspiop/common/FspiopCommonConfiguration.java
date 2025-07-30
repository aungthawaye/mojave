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
package io.mojaloop.fspiop.common;

import io.mojaloop.fspiop.common.data.ParticipantDetails;
import org.springframework.context.annotation.Bean;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

public class FspiopCommonConfiguration {

    @Bean
    public ParticipantDetails participantSettings(FspiopCommonConfiguration.Settings settings)
        throws NoSuchAlgorithmException, InvalidKeySpecException {

        return ParticipantDetails.with(settings.fspCode(),
                                       settings.fspName(),
                                       settings.ilpSecret(),
                                       settings.signJws(),
                                       settings.verifyJws(),
                                       settings.base64PrivateKey(),
                                       settings.base64FspPublicKeys());
    }

    public interface RequiredSettings {

        FspiopCommonConfiguration.Settings fspiopCommonSettings();

    }

    public record Settings(String fspCode,
                           String fspName,
                           String ilpSecret,
                           boolean signJws,
                           boolean verifyJws,
                           String base64PrivateKey,
                           Map<String, String> base64FspPublicKeys) { }

}
