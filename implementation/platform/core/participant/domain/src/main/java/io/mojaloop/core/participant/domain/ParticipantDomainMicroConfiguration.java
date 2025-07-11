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
package io.mojaloop.core.participant.domain;

import io.mojaloop.common.component.persistence.RoutingJpaConfiguration;
import io.mojaloop.common.component.vault.Vault;
import io.mojaloop.common.component.vault.VaultConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(value = {RoutingJpaConfiguration.class, VaultConfiguration.class})
public class ParticipantDomainMicroConfiguration implements RoutingJpaConfiguration.SettingsProvider, VaultConfiguration.SettingsProvider {

    private final Vault vault;

    public ParticipantDomainMicroConfiguration(Vault vault) {

        this.vault = vault;
    }

    @Bean
    @Override
    public RoutingJpaConfiguration.JpaSettings routingJpaConfigurationJpaSettings() {

        return this.vault.get("micro/participant/mysql/jpa/settings", RoutingJpaConfiguration.JpaSettings.class);
    }

    @Bean
    @Override
    public RoutingJpaConfiguration.ReadSettings routingJpaConfigurationReadSettings() {

        return this.vault.get("micro/participant/mysql/read/settings", RoutingJpaConfiguration.ReadSettings.class);
    }

    @Bean
    @Override
    public RoutingJpaConfiguration.WriteSettings routingJpaConfigurationWriteSettings() {

        return this.vault.get("micro/participant/mysql/write/settings", RoutingJpaConfiguration.WriteSettings.class);
    }

    @Override
    public VaultConfiguration.Settings vaultConfigurationSettings() {

        return VaultConfiguration.Settings.withPropertyOrEnv();
    }

}
