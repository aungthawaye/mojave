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

package org.mojave.core.lookup.service;

import org.mojave.component.web.spring.security.SpringSecurityConfigurer;
import org.mojave.core.participant.intercom.client.service.ParticipantIntercomService;
import org.mojave.fspiop.common.FspiopCommonConfiguration;
import org.mojave.fspiop.service.FspiopServiceConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;

final class LookUpServiceSettings implements LookUpServiceConfiguration.RequiredSettings {

    @Bean
    @Override
    public FspiopCommonConfiguration.ParticipantSettings fspiopCommonParticipantSettings() {

        var fspCode = System.getenv("FSPIOP_FSP_CODE");
        var fspName = System.getenv("FSPIOP_FSP_NAME");
        var ilpSecret = System.getenv("FSPIOP_ILP_SECRET");
        var signJws = Boolean.parseBoolean(System.getenv("FSPIOP_SIGN_JWS"));
        var verifyJws = Boolean.parseBoolean(System.getenv("FSPIOP_VERIFY_JWS"));
        var privateKeyPem = System.getenv("FSPIOP_PRIVATE_KEY_PEM");
        var fsps = System.getenv("FSPIOP_FSPS").split(",", -1);
        var fspPublicKeyPem = new HashMap<String, String>();

        for (var fsp : fsps) {

            var env = "FSPIOP_PUBLIC_KEY_PEM_OF_" + fsp.toUpperCase();
            var publicKeyPem = System.getenv(env);

            if (publicKeyPem != null) {
                fspPublicKeyPem.put(fsp, publicKeyPem);
            }
        }

        return new FspiopCommonConfiguration.ParticipantSettings(
            fspCode, fspName, ilpSecret, signJws, verifyJws, privateKeyPem, fspPublicKeyPem);

    }

    @Bean
    @Override
    public LookUpServiceConfiguration.TomcatSettings lookUpServiceTomcatSettings() {

        return new LookUpServiceConfiguration.TomcatSettings(
            Integer.parseInt(System.getenv("LOOKUP_SERVICE_PORT")));
    }

    @Bean
    @Override
    public ParticipantIntercomService.Settings participantIntercomServiceSettings() {

        return new ParticipantIntercomService.Settings(
            System.getenv("PARTICIPANT_INTERCOM_BASE_URL"));
    }

    @Bean
    @Override
    public FspiopServiceConfiguration.ServiceSettings serviceSettings() {

        return new FspiopServiceConfiguration.ServiceSettings(
            Integer.parseInt(System.getenv("FSPIOP_SERVICE_REQUEST_AGE_MS")),
            Boolean.parseBoolean(System.getenv("FSPIOP_SERVICE_REQUEST_AGE_VERIFICATION")));
    }

    @Bean
    @Override
    public SpringSecurityConfigurer.Settings springSecuritySettings() {

        return new SpringSecurityConfigurer.Settings(new String[]{"/parties/**"});
    }

}
