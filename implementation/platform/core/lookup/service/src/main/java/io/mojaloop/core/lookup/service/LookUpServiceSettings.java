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

import io.mojaloop.component.spring.security.SpringSecurityConfigurer;
import io.mojaloop.component.vault.Vault;
import io.mojaloop.component.vault.VaultConfiguration;
import io.mojaloop.core.lookup.domain.LookUpDomainSettings;
import io.mojaloop.fspiop.common.FspiopCommonConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(value = {VaultConfiguration.class})
public class LookUpServiceSettings  implements LookUpServiceConfiguration.RequiredSettings {

    private final Vault vault;

    public LookUpServiceSettings(Vault vault) {

        this.vault = vault;
    }

    @Override
    public FspiopCommonConfiguration.Settings fspiopCommonSettings() {
        // LookUpDomainSettings already has configured this.
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Bean
    @Override
    public LookUpServiceConfiguration.TomcatSettings lookUpServiceTomcatSettings() {

        return this.vault.get(VaultPaths.TOMCAT_SETTINGS, LookUpServiceConfiguration.TomcatSettings.class);
    }

    @Bean
    @Override
    public SpringSecurityConfigurer.Settings springSecuritySettings() {

        return this.vault.get(VaultPaths.SPRING_SECURITY_SETTINGS, SpringSecurityConfigurer.Settings.class);
    }

    public static class VaultPaths {

        public static final String TOMCAT_SETTINGS = "micro/core/lookup/service/tomcat/settings";

        public static final String SPRING_SECURITY_SETTINGS = "micro/core/lookup/service/spring-security/settings";

    }

}
