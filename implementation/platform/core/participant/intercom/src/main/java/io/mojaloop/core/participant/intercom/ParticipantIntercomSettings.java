package io.mojaloop.core.participant.intercom;

import io.mojaloop.component.jpa.routing.RoutingDataSourceConfigurer;
import io.mojaloop.component.jpa.routing.RoutingEntityManagerConfigurer;
import io.mojaloop.component.redis.RedissonOpsClientConfigurer;
import io.mojaloop.component.vault.Vault;
import io.mojaloop.component.vault.VaultConfiguration;
import io.mojaloop.component.web.security.spring.SpringSecurityConfigurer;
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

        return new RoutingEntityManagerConfigurer.Settings("participant-intercom", false, false);
    }

    @Bean
    @Override
    public SpringSecurityConfigurer.Settings springSecuritySettings() {

        return new SpringSecurityConfigurer.Settings(null);
    }

    @Bean
    @Override
    public ParticipantIntercomConfiguration.TomcatSettings tomcatSettings() {

        return new ParticipantIntercomConfiguration.TomcatSettings(
            Integer.parseInt(System.getenv().getOrDefault("PARTICIPANT_INTERCOM_PORT", "4201")));
    }

    public static class VaultPaths {

        public static final String FLYWAY_PATH = "micro/core/participant/intercom/flyway/migration/settings";

        public static final String ROUTING_DATASOURCE_READ_SETTINGS_PATH = "micro/core/participant/intercom/routing-datasource/read/settings";

        public static final String ROUTING_DATASOURCE_WRITE_SETTINGS_PATH = "micro/core/participant/intercom/routing-datasource/write/settings";

        public static final String REDIS_OPS_SETTINGS_PATH = "micro/core/participant/intercom/redis/ops/settings";

    }

}
