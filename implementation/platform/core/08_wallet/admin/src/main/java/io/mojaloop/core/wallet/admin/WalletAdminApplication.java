package io.mojaloop.core.wallet.admin;

import io.mojaloop.component.flyway.FlywayMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@Import(value = {WalletAdminConfiguration.class, WalletAdminSettings.class})
public class WalletAdminApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletAdminApplication.class);

    public static void main(String[] args) {

        LOGGER.info("Starting wallet-admin application");

        var flywaySettings = new FlywayMigration.Settings(System.getenv()
                                                                .getOrDefault("WLT_FLYWAY_DB_URL",
                                                                              "jdbc:mysql://localhost:3306/ml_wallet?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC"),
                                                          System.getenv().getOrDefault("WLT_FLYWAY_DB_USER", "root"),
                                                          System.getenv().getOrDefault("WLT_FLYWAY_DB_PASSWORD", "password"), "classpath:migration/wallet");

        LOGGER.info("Flyway migration settings: {}", flywaySettings);
        FlywayMigration.migrate(flywaySettings);
        LOGGER.info("Flyway migration completed");

        new SpringApplicationBuilder(WalletAdminApplication.class).web(WebApplicationType.SERVLET)
                                                                  .properties("spring.application.name=wallet-admin", "spring.jmx.enabled=true",
                                                                              "spring.jmx.unique-types=true", "spring.jmx.default-domain=wallet-admin",
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
                                                                              "spring.application.admin.jmx-name=org.springframework.boot:type=Admin,name=WalletAdminApplication,context=wallet-admin")
                                                                  .run(args);
    }

}
