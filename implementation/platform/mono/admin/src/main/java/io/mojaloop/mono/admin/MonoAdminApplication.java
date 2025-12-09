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

package io.mojaloop.mono.admin;

import io.mojaloop.core.accounting.domain.AccountingFlyway;
import io.mojaloop.core.participant.domain.ParticipantFlyway;
import io.mojaloop.core.wallet.domain.WalletFlyway;
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
@Import(
    value = {
        MonoAdminConfiguration.class,
        MonoAdminDependencies.class,
        MonoAdminSettings.class})
public class MonoAdminApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonoAdminApplication.class);

    public static void main(String[] args) {

        ParticipantFlyway.migrate(
            System.getenv("MONO_FLYWAY_DB_URL"),
            System.getenv("MONO_FLYWAY_DB_USER"), System.getenv("MONO_FLYWAY_DB_PASSWORD"));

        AccountingFlyway.migrate(
            System.getenv("MONO_FLYWAY_DB_URL"),
            System.getenv("MONO_FLYWAY_DB_USER"), System.getenv("MONO_FLYWAY_DB_PASSWORD"));

        WalletFlyway.migrate(
            System.getenv("MONO_FLYWAY_DB_URL"),
            System.getenv("MONO_FLYWAY_DB_USER"), System.getenv("MONO_FLYWAY_DB_PASSWORD"));

        new SpringApplicationBuilder(MonoAdminApplication.class)
            .web(WebApplicationType.SERVLET)
            .properties(
                "spring.application.name=mono-admin", "spring.jmx.enabled=true",
                "spring.jmx.unique-types=true", "spring.jmx.default-domain=mono-admin",
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
                "spring.application.admin.jmx-name=org.springframework.boot:type=Admin,name=MonoAdminApplication,context=mono-admin")
            .run(args);
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer(
        MonoAdminConfiguration.TomcatSettings settings) {

        return factory -> factory.setPort(settings.portNo());
    }

}
