package io.mojaloop.core.participant.store.settings;

import io.mojaloop.common.component.redis.RedissonOpsClientConfigurer;
import io.mojaloop.common.component.vault.Vault;
import io.mojaloop.common.component.vault.VaultConfiguration;
import io.mojaloop.core.participant.store.ParticipantStoreConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(value = {VaultConfiguration.class})
public class ParticipantStoreVaultBasedSettings implements ParticipantStoreConfiguration.RequiredSettings {

    private final Vault vault;

    public ParticipantStoreVaultBasedSettings(Vault vault) {

        assert vault != null;

        this.vault = vault;
    }

    @Bean
    @Override
    public RedissonOpsClientConfigurer.Settings redissonOpsClientSettings() {

        return this.vault.get(VaultPaths.REDIS_OPS_SETTINGS_PATH, RedissonOpsClientConfigurer.Settings.class);
    }

    public static class VaultPaths {

        public static final String REDIS_OPS_SETTINGS_PATH = "micro/core/participant/store/redis/ops/settings";

    }

}
