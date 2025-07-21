package io.mojaloop.core.participant.domain.settings;

import io.mojaloop.common.component.persistence.routing.RoutingDataSourceConfigurer;
import io.mojaloop.common.component.persistence.routing.RoutingEntityManagerConfigurer;
import io.mojaloop.common.component.redis.RedissonOpsClientConfigurer;
import io.mojaloop.common.component.vault.Vault;
import io.mojaloop.common.component.vault.VaultConfiguration;
import io.mojaloop.core.participant.domain.ParticipantDomainConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(value = {VaultConfiguration.class})
public class ParticipantVaultBasedSettings implements ParticipantDomainConfiguration.RequiredSettings {

    private final Vault vault;

    public ParticipantVaultBasedSettings(Vault vault) {

        assert vault != null;

        this.vault = vault;
    }

    @Bean
    @Override
    public RedissonOpsClientConfigurer.Settings redissonOpsClientSettings() {

        return this.vault.get(VaultPaths.REDIS_OPS_SETTINGS_PATH, RedissonOpsClientConfigurer.Settings.class);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        return this.vault.get(VaultPaths.ROUTING_DATASOURCE_READ_SETTINGS_PATH, RoutingDataSourceConfigurer.ReadSettings.class);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        return this.vault.get(VaultPaths.ROUTING_DATASOURCE_WRITE_SETTINGS_PATH, RoutingDataSourceConfigurer.WriteSettings.class);
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return this.vault.get(VaultPaths.ENTITY_MANAGER_SETTINGS_PATH, RoutingEntityManagerConfigurer.Settings.class);
    }

    public static class VaultPaths {

        public static final String FLYWAY_PATH = "micro/core/participant/domain/flyway/migration/settings";

        public static final String ROUTING_DATASOURCE_READ_SETTINGS_PATH = "micro/core/participant/domain/routing-datasource/read/settings";

        public static final String ROUTING_DATASOURCE_WRITE_SETTINGS_PATH = "micro/core/participant/domain/routing-datasource/write/settings";

        public static final String ENTITY_MANAGER_SETTINGS_PATH = "micro/core/participant/domain/entity-manager/settings";

        public static final String REDIS_OPS_SETTINGS_PATH = "micro/core/participant/domain/redis/ops/settings";

    }

}