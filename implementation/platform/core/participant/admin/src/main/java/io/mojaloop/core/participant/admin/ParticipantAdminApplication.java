package io.mojaloop.core.participant.admin;

import io.mojaloop.component.flyway.FlywayMigration;
import io.mojaloop.component.vault.Vault;
import io.mojaloop.component.vault.VaultConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = "io.mojaloop.core.participant.admin")
@Import(value = {ParticipantAdminConfiguration.class, ParticipantAdminApplication.VaultSettings.class, ParticipantAdminSettings.class})
public class ParticipantAdminApplication {

    private final static Logger LOGGER = LoggerFactory.getLogger(ParticipantAdminApplication.class.getName());

    public static void main(String[] args) {

        LOGGER.info("Starting participant admin application");

        var vaultSettings = VaultConfigurer.Settings.withPropertyOrEnv();
        var vault = new Vault(vaultSettings.address(), vaultSettings.token(), vaultSettings.enginePath());
        var flywaySettings = vault.get(ParticipantAdminSettings.VaultPaths.FLYWAY_PATH, FlywayMigration.Settings.class);

        LOGGER.info("Flyway migration settings: {}", flywaySettings);
        FlywayMigration.migrate(flywaySettings);
        LOGGER.info("Flyway migration completed");

        new SpringApplicationBuilder(ParticipantAdminApplication.class)
            .web(WebApplicationType.SERVLET)
            .properties(
                "spring.application.name=participant-admin",
                "spring.jmx.enabled=true",
                "spring.jmx.unique-names=true",
                "spring.jmx.default-domain=participant-admin",
                "spring.application.admin.enabled=true",
                "management.endpoints.web.base-path=/actuator",
                "management.endpoint.health.show-details=always",
                "management.endpoint.health.group.readiness.include=db,diskSpace,process,throttling",
                "management.endpoint.health.group.liveness.include=db,diskSpace,process,throttling",
                "management.endpoint.health.group.throttling.include=throttling",
                "management.endpoint.throttling.enabled=true",
                "management.endpoint.health.validate-group-membership=false",
                "management.endpoint.health.probes.enabled=true",
                "management.endpoints.web.exposure.include=health,info,metrics,prometheus",
                "management.endpoint.health.show-details=always",
                "spring.application.admin.jmx-name=org.springframework.boot:type=Admin,name=ParticipantAdminApplication,context=participant-admin")
            .run(args);
    }

    public static class VaultSettings {

        @Bean
        public VaultConfigurer.Settings vaultSettings() {

            return VaultConfigurer.Settings.withPropertyOrEnv();
        }

    }

}
