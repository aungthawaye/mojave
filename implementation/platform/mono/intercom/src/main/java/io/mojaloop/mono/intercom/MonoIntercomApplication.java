package io.mojaloop.mono.intercom;

import io.mojaloop.component.flyway.FlywayMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@Import(value = {MonoIntercomConfiguration.class, MonoIntercomSettings.class})
public class MonoIntercomApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonoIntercomApplication.class);

    public static void main(String[] args) {

        LOGGER.info("Starting mojave intercom application");

        var flywaySettings = new FlywayMigration.Settings(System.getenv()
                                                                .getOrDefault("MONO_FLYWAY_DB_URL",
                                                                    "jdbc:mysql://localhost:3306/ml_mojave?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC"),
            System.getenv().getOrDefault("MONO_FLYWAY_DB_USER", "root"), System.getenv().getOrDefault("MONO_FLYWAY_DB_PASSWORD", "password"), "classpath:migration/participant",
            "classpath:migration/accounting", "classpath:migration/wallet", "classpath:migration/transaction");

        LOGGER.info("Flyway migration settings: {}", flywaySettings);
        FlywayMigration.migrate(flywaySettings);
        LOGGER.info("Flyway migration completed");

        new SpringApplicationBuilder(MonoIntercomApplication.class).web(WebApplicationType.SERVLET)
                                                                   .properties("spring.application.name=mono-intercom", "spring.jmx.enabled=true", "spring.jmx.unique-types=true",
                                                                       "spring.jmx.default-domain=mono-intercom", "spring.application.admin.enabled=true",
                                                                       "management.endpoints.web.base-path=/actuator", "management.endpoint.health.show-details=always",
                                                                       "management.endpoint.health.group.readiness.include=db,diskSpace,process,throttling",
                                                                       "management.endpoint.health.group.liveness.include=db,diskSpace,process,throttling",
                                                                       "management.endpoint.health.group.throttling.include=throttling",
                                                                       "management.endpoint.throttling.enabled=true", "management.endpoint.health.validate-group-membership=false",
                                                                       "management.endpoint.health.probes.enabled=true",
                                                                       "management.endpoints.web.exposure.include=health,info,metrics,prometheus",
                                                                       "management.endpoint.health.show-details=always",
                                                                       "spring.application.admin.jmx-name=org.springframework.boot:type=Admin,name=MonoIntercomApplication,context=mono-intercom")
                                                                   .run(args);
    }

}
