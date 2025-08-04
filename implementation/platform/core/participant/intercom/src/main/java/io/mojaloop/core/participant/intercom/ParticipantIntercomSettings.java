package io.mojaloop.core.participant.intercom;

import io.mojaloop.component.jpa.routing.RoutingDataSourceConfigurer;
import io.mojaloop.component.jpa.routing.RoutingEntityManagerConfigurer;
import io.mojaloop.component.redis.RedissonOpsClientConfigurer;
import io.mojaloop.component.vault.Vault;
import io.mojaloop.component.vault.VaultConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(value = {VaultConfiguration.class})
final class ParticipantIntercomSettings implements ParticipantIntercomConfiguration.RequiredSettings {

    private final Vault vault;

    public ParticipantIntercomSettings(Vault vault) {

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
    public ParticipantIntercomConfiguration.TomcatSettings tomcatSettings() {

        return this.vault.get(VaultPaths.TOMCAT_SETTINGS, ParticipantIntercomConfiguration.TomcatSettings.class);
    }

    public static class VaultPaths {

        public static final String TOMCAT_SETTINGS = "micro/core/participant/intercom/tomcat/settings";

        public static final String ROUTING_DATASOURCE_READ_SETTINGS_PATH = "micro/core/participant/intercom/routing-datasource/read/settings";

        public static final String ROUTING_DATASOURCE_WRITE_SETTINGS_PATH = "micro/core/participant/intercom/routing-datasource/write/settings";

        public static final String ENTITY_MANAGER_SETTINGS_PATH = "micro/core/participant/intercom/entity-manager/settings";

        public static final String REDIS_OPS_SETTINGS_PATH = "micro/core/participant/intercom/redis/ops/settings";

    }

}
