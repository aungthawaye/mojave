/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */

package org.mojave.core.mono.intercom;

import org.mojave.core.participant.domain.ParticipantFlyway;
import org.mojave.core.settlement.domain.SettlementFlyway;
import org.mojave.core.wallet.domain.WalletFlyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration(
    exclude = {
        SecurityAutoConfiguration.class,
        JacksonAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class})
@Import(
    value = {
        MonoIntercomConfiguration.class,
        MonoIntercomDependencies.class,
        MonoIntercomSettings.class})
public class MonoIntercomApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonoIntercomApplication.class);

    public static void main(String[] args) {

        ParticipantFlyway.migrate(
            System.getenv("FLYWAY_DB_URL"), System.getenv("FLYWAY_DB_USER"),
            System.getenv("FLYWAY_DB_PASSWORD"));

        SettlementFlyway.migrate(
            System.getenv("FLYWAY_DB_URL"), System.getenv("FLYWAY_DB_USER"),
            System.getenv("FLYWAY_DB_PASSWORD"));

        WalletFlyway.migrate(
            System.getenv("FLYWAY_DB_URL"), System.getenv("FLYWAY_DB_USER"),
            System.getenv("FLYWAY_DB_PASSWORD"));

        new SpringApplicationBuilder(MonoIntercomApplication.class)
            .web(WebApplicationType.SERVLET)
            .properties(
                "spring.application.name=mono-intercom",
                "management.endpoints.web.base-path=/actuator",
                "management.endpoint.health.show-details=always",
                "management.endpoint.health.group.readiness.include=db,diskSpace,process,throttling",
                "management.endpoint.health.group.liveness.include=db,diskSpace,process,throttling",
                "management.endpoint.health.group.throttling.include=throttling",
                "management.endpoint.throttling.enabled=true",
                "management.endpoint.health.validate-group-membership=false",
                "management.endpoint.health.probes.enabled=true",
                "management.endpoints.web.exposure.include=health,info,metrics,prometheus",
                "management.endpoint.health.show-details=always")
            .run(args);
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer(
        MonoIntercomConfiguration.TomcatSettings settings) {

        return factory -> factory.setPort(settings.portNo());
    }

}
