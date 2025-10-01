package io.mojaloop.core.quoting.service;

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
@Import(value = {QuotingServiceConfiguration.class, QuotingServiceSettings.class})
public class QuotingServiceApplication {

    private final static Logger LOGGER = LoggerFactory.getLogger(QuotingServiceApplication.class);

    public static void main(String[] args) {

        LOGGER.info("Starting participant-admin application");

        var flywaySettings = new FlywayMigration.Settings(System.getenv()
                                                                .getOrDefault("QOT_FLYWAY_DB_URL",
                                                                              "jdbc:mysql://localhost:3306/ml_quoting?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC"),
                                                          System.getenv().getOrDefault("QOT_FLYWAY_DB_USER", "root"),
                                                          System.getenv().getOrDefault("QOT_FLYWAY_DB_PASSWORD", "password"),
                                                          "classpath:migration/quoting");

        LOGGER.info("Flyway migration settings: {}", flywaySettings);
        FlywayMigration.migrate(flywaySettings);
        LOGGER.info("Flyway migration completed");

        new SpringApplicationBuilder(QuotingServiceApplication.class)
            .web(WebApplicationType.SERVLET)
            .properties(
                "spring.application.name=quoting-service",
                "spring.jmx.enabled=true",
                "spring.jmx.unique-names=true",
                "spring.jmx.default-domain=quoting-service",
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
                "spring.application.admin.jmx-name=org.springframework.boot:type=Admin,name=QuotingServiceApplication,context=quoting-service")
            .run(args);
    }

}