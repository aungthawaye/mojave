/*-
 * ================================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */

package io.mojaloop.platform.core.transfer.service;

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
@Import(value = {TransferServiceConfiguration.class, TransferServiceSettings.class})
public class TransferServiceApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferServiceApplication.class);

    public static void main(String[] args) {

        LOGGER.info("Starting transfer-service application");

        var flywaySettings = new FlywayMigration.Settings(System.getenv()
                                                                .getOrDefault("TFR_FLYWAY_DB_URL",
                                                                    "jdbc:mysql://localhost:3306/ml_transfer?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC"),
            System.getenv().getOrDefault("TFR_FLYWAY_DB_USER", "root"), System.getenv().getOrDefault("TFR_FLYWAY_DB_PASSWORD", "password"), "classpath:migration/transfer");

        LOGGER.info("Flyway migration settings: {}", flywaySettings);
        FlywayMigration.migrate(flywaySettings);
        LOGGER.info("Flyway migration completed");

        new SpringApplicationBuilder(TransferServiceApplication.class).web(WebApplicationType.SERVLET)
                                                                      .properties("spring.application.name=transfer-service", "spring.jmx.enabled=true",
                                                                          "spring.jmx.unique-types=true", "spring.jmx.default-domain=transfer-service",
                                                                          "spring.application.admin.enabled=true", "management.endpoints.web.base-path=/actuator",
                                                                          "management.endpoint.health.show-details=always",
                                                                          "management.endpoint.health.group.readiness.include=db,diskSpace,process,throttling",
                                                                          "management.endpoint.health.group.liveness.include=db,diskSpace,process,throttling",
                                                                          "management.endpoint.health.group.throttling.include=throttling",
                                                                          "management.endpoint.throttling.enabled=true",
                                                                          "management.endpoint.health.validate-group-membership=false",
                                                                          "management.endpoint.health.probes.enabled=true",
                                                                          "management.endpoints.web.exposure.include=health,info,metrics,prometheus",
                                                                          "management.endpoint.health.show-details=always",
                                                                          "spring.application.admin.jmx-name=org.springframework.boot:type=Admin,name=TransferServiceApplication,context=transfer-service")
                                                                      .run(args);
    }

}
