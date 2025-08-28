package io.mojaloop.core.participant.admin;

import io.mojaloop.component.jpa.routing.RoutingDataSourceConfigurer;
import io.mojaloop.component.jpa.routing.RoutingEntityManagerConfigurer;
import io.mojaloop.component.redis.RedissonOpsClientConfigurer;
import io.mojaloop.component.vault.Vault;
import io.mojaloop.component.vault.VaultConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(value = {VaultConfiguration.class})
public class ParticipantAdminSettings implements ParticipantAdminConfiguration.RequiredSettings {

    private final Vault vault;

    public ParticipantAdminSettings(Vault vault) {

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

    @Bean
    @Override
    public ParticipantAdminConfiguration.TomcatSettings tomcatSettings() {

        return this.vault.get(VaultPaths.TOMCAT_SETTINGS, ParticipantAdminConfiguration.TomcatSettings.class);
    }

    public static class VaultPaths {

        public static final String TOMCAT_SETTINGS = "micro/core/participant/admin/tomcat/settings";

        public static final String ROUTING_DATASOURCE_READ_SETTINGS_PATH = "micro/core/participant/admin/routing-datasource/read/settings";

        public static final String ROUTING_DATASOURCE_WRITE_SETTINGS_PATH = "micro/core/participant/admin/routing-datasource/write/settings";

        public static final String ENTITY_MANAGER_SETTINGS_PATH = "micro/core/participant/admin/entity-manager/settings";

        public static final String REDIS_OPS_SETTINGS_PATH = "micro/core/participant/admin/redis/ops/settings";

    }

}
