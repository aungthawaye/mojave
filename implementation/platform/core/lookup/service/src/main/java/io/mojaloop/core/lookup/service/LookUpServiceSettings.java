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

package io.mojaloop.core.lookup.service;

import io.mojaloop.component.vault.Vault;
import io.mojaloop.component.vault.VaultConfiguration;
import io.mojaloop.component.web.spring.security.SpringSecurityConfigurer;
import io.mojaloop.core.participant.intercom.client.service.ParticipantIntercomService;
import io.mojaloop.core.participant.store.ParticipantStoreConfiguration;
import io.mojaloop.fspiop.common.FspiopCommonConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(value = {VaultConfiguration.class})
final class LookUpServiceSettings implements LookUpServiceConfiguration.RequiredSettings {

    private final Vault vault;

    public LookUpServiceSettings(Vault vault) {

        this.vault = vault;
    }

    @Bean
    @Override
    public FspiopCommonConfiguration.ParticipantSettings fspiopCommonParticipantSettings() {

        return this.vault.get(VaultPaths.FSPIOP_SETTINGS, FspiopCommonConfiguration.ParticipantSettings.class);
    }

    @Bean
    @Override
    public LookUpServiceConfiguration.TomcatSettings lookUpServiceTomcatSettings() {

        return new LookUpServiceConfiguration.TomcatSettings(Integer.parseInt(System.getenv().getOrDefault("LOOKUP_SERVICE_PORT", "4303")));
    }

    @Bean
    @Override
    public ParticipantIntercomService.Settings participantIntercomServiceSettings() {

        return new ParticipantIntercomService.Settings(
            System.getenv().getOrDefault("PARTICIPANT_INTERCOM_BASE_URL", "http://localhost:4201"));
    }

    @Bean
    @Override
    public ParticipantStoreConfiguration.Settings participantStoreSettings() {

        return new ParticipantStoreConfiguration.Settings(
            Integer.parseInt(System.getenv().getOrDefault("PARTICIPANT_STORE_REFRESH_INTERVAL_MS", "300000")));
    }

    @Bean
    @Override
    public SpringSecurityConfigurer.Settings springSecuritySettings() {

        return new SpringSecurityConfigurer.Settings(new String[]{"/parties/**"});
    }

    public static class VaultPaths {

        public static final String FSPIOP_SETTINGS = "micro/fspiop/common/participant/settings";

    }

}
