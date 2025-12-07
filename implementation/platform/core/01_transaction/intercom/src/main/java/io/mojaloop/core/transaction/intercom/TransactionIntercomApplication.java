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

package io.mojaloop.core.transaction.intercom;

import io.mojaloop.core.transaction.domain.TransactionFlyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration(
    exclude = {
        SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class,
        FlywayAutoConfiguration.class})
@Import(value = {TransactionIntercomConfiguration.class, TransactionIntercomSettings.class})
public class TransactionIntercomApplication {

    private final static Logger LOGGER = LoggerFactory.getLogger(
        TransactionIntercomApplication.class.getName());

    public static void main(String[] args) {

        TransactionFlyway.migrate(
            System.getenv("TXN_FLYWAY_DB_URL"), System.getenv("TXN_FLYWAY_DB_USER"),
            System.getenv("TXN_FLYWAY_DB_PASSWORD"));

        new SpringApplicationBuilder(TransactionIntercomApplication.class)
            .web(WebApplicationType.SERVLET)
            .properties(
                "spring.application.name=transaction-intercom", "spring.jmx.enabled=true",
                "spring.jmx.unique-types=true", "spring.jmx.default-domain=transaction-intercom",
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
                "spring.application.admin.jmx-name=org.springframework.boot:type=Admin,name=TransactionIntercomApplication,context=transaction-intercom")
            .run(args);
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer(
        TransactionIntercomConfiguration.TomcatSettings settings) {

        return factory -> factory.setPort(settings.portNo());
    }

}
