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

package io.mojaloop.core.lookup.domain;

import io.mojaloop.component.vault.Vault;
import io.mojaloop.component.vault.VaultConfiguration;
import io.mojaloop.core.participant.utility.client.ParticipantClient;
import io.mojaloop.fspiop.common.FspiopCommonConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(value = {VaultConfiguration.class})
class LookUpDomainSettings implements LookUpDomainConfiguration.RequiredSettings {

    private final Vault vault;

    public LookUpDomainSettings(Vault vault) {

        assert vault != null;

        this.vault = vault;
    }

    @Bean
    @Override
    public FspiopCommonConfiguration.Settings fspiopCommonSettings() {

        return this.vault.get(VaultPaths.FSPIOP_SETTINGS, FspiopCommonConfiguration.Settings.class);
    }

    @Bean
    @Override
    public ParticipantClient.Settings participantClientSettings() {

        return this.vault.get(VaultPaths.PARTICIPANT_CLIENT_SETTING, ParticipantClient.Settings.class);
    }

    public static class VaultPaths {

        public static final String FSPIOP_SETTINGS = "micro/core/lookup/domain/fspiop/settings";

        public static final String PARTICIPANT_CLIENT_SETTING = "micro/core/lookup/domain/participant-client/settings";

    }

}
