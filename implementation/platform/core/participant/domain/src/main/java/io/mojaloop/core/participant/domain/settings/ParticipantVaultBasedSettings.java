package io.mojaloop.core.participant.domain.settings;

import io.mojaloop.common.component.persistence.routing.RoutingDataSourceConfigurer;
import io.mojaloop.common.component.persistence.routing.RoutingEntityManagerConfigurer;
import io.mojaloop.common.component.redis.RedissonOpsClientConfigurer;
import io.mojaloop.common.component.vault.Vault;
import io.mojaloop.common.component.vault.VaultConfiguration;
import io.mojaloop.core.participant.domain.ParticipantDomainMicroConfiguration;
import org.springframework.context.annotation.Import;

@Import(value = {VaultConfiguration.class})
public class ParticipantVaultBasedSettings  {

    private final Vault vault;

    public ParticipantVaultBasedSettings(Vault vault) {

        assert vault != null;

        this.vault = vault;
    }

    
    public RoutingDataSourceConfigurer.ReadSettings dataSourceReadSettings() {

        return this.vault.get(VaultPaths.ROUTING_DATASOURCE_READ_SETTINGS_PATH, RoutingDataSourceConfigurer.ReadSettings.class);
    }

    
    public RoutingDataSourceConfigurer.WriteSettings dataSourceWriteSettings() {

        return this.vault.get(VaultPaths.ROUTING_DATASOURCE_WRITE_SETTINGS_PATH, RoutingDataSourceConfigurer.WriteSettings.class);
    }

    
    public RoutingEntityManagerConfigurer.Settings entityManagerSettings() {

        return this.vault.get(VaultPaths.ENTITY_MANAGER_SETTINGS_PATH, RoutingEntityManagerConfigurer.Settings.class);
    }

    
    public RedissonOpsClientConfigurer.Settings redissonOpsSettings() {

        return this.vault.get(VaultPaths.REDIS_OPS_SETTINGS_PATH, RedissonOpsClientConfigurer.Settings.class);
    }

    public static class VaultPaths {

        public static final String FLYWAY_PATH = "micro/core/participant/domain/flyway/migration";

        public static final String ROUTING_DATASOURCE_READ_SETTINGS_PATH = "micro/core/participant/domain/routing-datasource/read/settings";

        public static final String ROUTING_DATASOURCE_WRITE_SETTINGS_PATH = "micro/core/participant/domain/routing-datasource/write/settings";

        public static final String ENTITY_MANAGER_SETTINGS_PATH = "micro/core/participant/domain/entity-manager/settings";

        public static final String REDIS_OPS_SETTINGS_PATH = "micro/core/participant/domain/redis/ops/settings";

    }

}