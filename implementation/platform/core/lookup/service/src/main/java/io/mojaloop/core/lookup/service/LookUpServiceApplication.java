/*-
 * ================================================================================
 * Mojaloop OSS
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

package io.mojaloop.core.lookup.service;

import io.mojaloop.component.vault.VaultConfigurer;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {LookUpServiceConfiguration.class, LookUpServiceApplication.VaultSettings.class, LookUpServiceSettings.class})
public class LookUpServiceApplication {

    public static void main(String[] args) {

        new SpringApplicationBuilder(LookUpServiceApplication.class)
            .web(WebApplicationType.SERVLET)
            .properties("spring.application.name=lookup-service", "spring.jmx.enabled=true", "spring.jmx.unique-names=true",
                        "spring.jmx.default-domain=lookup-service", "spring.application.admin.enabled=true",
                        "management.endpoints.web.base-path=/actuator",
                        "management.endpoints.web.exposure.include=health,info,metrics,prometheus",
                        "spring.application.admin.jmx-name=org.springframework.boot:type=Admin,name=LookUpServiceApplication,context=lookup-service")
            .run(args);
    }

    public static class VaultSettings {

        @Bean
        public VaultConfigurer.Settings vaultSettings() {

            return VaultConfigurer.Settings.withPropertyOrEnv();
        }

    }

}
