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

import io.mojaloop.component.redis.RedissonOpsClientConfigurer;
import io.mojaloop.component.vault.Vault;
import io.mojaloop.component.vault.VaultConfiguration;
import io.mojaloop.component.web.security.spring.SpringSecurityConfigurer;
import io.mojaloop.fspiop.common.FspiopCommonConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(value = {VaultConfiguration.class})
class LookUpServiceSettings implements LookUpServiceConfiguration.RequiredSettings {

    private final Vault vault;

    public LookUpServiceSettings(Vault vault) {

        this.vault = vault;
    }

    @Bean
    @Override
    public FspiopCommonConfiguration.Settings fspiopCommonSettings() {

        return this.vault.get(VaultPaths.FSPIOP_SETTINGS, FspiopCommonConfiguration.Settings.class);
    }

    @Bean
    @Override
    public LookUpServiceConfiguration.TomcatSettings lookUpServiceTomcatSettings() {

        return this.vault.get(VaultPaths.TOMCAT_SETTINGS, LookUpServiceConfiguration.TomcatSettings.class);
    }

    @Bean
    @Override
    public RedissonOpsClientConfigurer.Settings redissonOpsClientSettings() {

        return this.vault.get(VaultPaths.REDIS_OPS_SETTINGS_PATH, RedissonOpsClientConfigurer.Settings.class);
    }

    @Bean
    @Override
    public SpringSecurityConfigurer.Settings springSecuritySettings() {

        return this.vault.get(VaultPaths.SPRING_SECURITY_SETTINGS, SpringSecurityConfigurer.Settings.class);
    }

    public static class VaultPaths {

        public static final String FSPIOP_SETTINGS = "micro/core/lookup/service/fspiop/settings";

        public static final String TOMCAT_SETTINGS = "micro/core/lookup/service/tomcat/settings";

        public static final String REDIS_OPS_SETTINGS_PATH = "micro/core/lookup/service/redis/ops/settings";

        public static final String SPRING_SECURITY_SETTINGS = "micro/core/lookup/service/spring-security/settings";

    }

}
