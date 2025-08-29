package io.mojaloop.core.participant.intercom;

import io.mojaloop.component.flyway.FlywayMigration;
import io.mojaloop.component.vault.Vault;
import io.mojaloop.component.vault.VaultConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(
    value = {ParticipantIntercomConfiguration.class, ParticipantIntercomApplication.VaultSettings.class, ParticipantIntercomSettings.class})
public class ParticipantIntercomApplication {

    private final static Logger LOGGER = LoggerFactory.getLogger(ParticipantIntercomApplication.class.getName());

    public static void main(String[] args) {

        LOGGER.info("Starting participant intercom application");

        var vaultSettings = VaultConfigurer.Settings.withPropertyOrEnv();
        var vault = new Vault(vaultSettings.address(), vaultSettings.token(), vaultSettings.enginePath());
        var flywaySettings = vault.get(ParticipantIntercomSettings.VaultPaths.FLYWAY_PATH, FlywayMigration.Settings.class);

        LOGGER.info("Flyway migration settings: {}", flywaySettings);
        FlywayMigration.migrate(flywaySettings);
        LOGGER.info("Flyway migration completed");

        new SpringApplicationBuilder(ParticipantIntercomApplication.class)
            .web(WebApplicationType.SERVLET)
            .properties("spring.application.name=participant-intercom", "spring.jmx.enabled=true", "spring.jmx.unique-names=true",
                        "spring.jmx.default-domain=participant-intercom", "spring.application.admin.enabled=true",
                        "management.endpoints.web.base-path=/actuator",
                        "management.endpoints.web.exposure.include=health,info,metrics,prometheus",
                        "spring.application.admin.jmx-name=org.springframework.boot:type=Admin,name=ParticipantIntercomApplication,context=participant-intercom")
            .run(args);
    }

    public static class VaultSettings {

        @Bean
        public VaultConfigurer.Settings vaultSettings() {

            return VaultConfigurer.Settings.withPropertyOrEnv();
        }

    }

}
