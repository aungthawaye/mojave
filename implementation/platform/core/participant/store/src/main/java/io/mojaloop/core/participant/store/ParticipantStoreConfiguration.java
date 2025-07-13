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
public class ParticipantStoreConfiguration {

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

            return () -> vault.get("common/central-cache/redis/settings", RedisOpsConfigurer.Settings.class);
        }

    }
}
