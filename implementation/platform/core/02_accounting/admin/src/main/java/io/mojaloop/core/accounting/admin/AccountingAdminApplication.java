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

package io.mojaloop.core.accounting.admin;

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
@Import(value = {AccountingAdminConfiguration.class, AccountingAdminSettings.class})
public class AccountingAdminApplication {

    private final static Logger LOGGER = LoggerFactory.getLogger(AccountingAdminApplication.class);

    public static void main(String[] args) {

        LOGGER.info("Starting accounting-admin application");
        var flywaySettings = new FlywayMigration.Settings(System.getenv()
                                                                .getOrDefault("ACC_FLYWAY_DB_URL",
                                                                    "jdbc:mysql://localhost:3306/ml_accounting?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC"),
            System.getenv().getOrDefault("ACC_FLYWAY_DB_USER", "root"), System.getenv().getOrDefault("ACC_FLYWAY_DB_PASSWORD", "password"), "classpath:migration/accounting");

        LOGGER.info("Flyway migration settings: {}", flywaySettings);
        FlywayMigration.configure(flywaySettings);
        LOGGER.info("Flyway migration completed");

        new SpringApplicationBuilder(AccountingAdminApplication.class).web(WebApplicationType.SERVLET)
                                                                      .properties("spring.application.name=accounting-admin", "spring.jmx.enabled=true",
                                                                          "spring.jmx.unique-types=true", "spring.jmx.default-domain=accounting-admin",
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
                                                                          "spring.application.admin.jmx-name=org.springframework.boot:type=Admin,name=AccountingAdminApplication,context=accounting-admin")
                                                                      .run(args);
    }

}
