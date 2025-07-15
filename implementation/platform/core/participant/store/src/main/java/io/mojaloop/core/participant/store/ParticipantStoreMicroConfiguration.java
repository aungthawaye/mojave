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
package io.mojaloop.core.participant.store;

import io.mojaloop.common.component.redis.RedisOpsConfigurer;
import io.mojaloop.common.component.vault.Vault;
import io.mojaloop.common.component.vault.VaultConfiguration;
import io.mojaloop.core.participant.store.qualifier.Qualifiers;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = "io.mojaloop.common.centralcache")
public class ParticipantStoreMicroConfiguration {

    @Bean
    @Qualifier(Qualifiers.CENTRAL_CACHE_OPS)
    public RedissonClient redissonOpsClient(@Qualifier(Qualifiers.CENTRAL_CACHE_OPS) RedisOpsConfigurer.SettingsProvider settingsProvider) {

        return new RedisOpsConfigurer().configure(settingsProvider);
    }

    @Import(value = {VaultConfiguration.class})
    public static class VaultBasedSettings {

        @Bean
        @Qualifier(Qualifiers.CENTRAL_CACHE_OPS)
        public RedisOpsConfigurer.SettingsProvider redisOpsConfigurationSettings(Vault vault) {

            return () -> vault.get("micro/core/participant/store/redis/ops/settings", RedisOpsConfigurer.Settings.class);
        }

    }
}
