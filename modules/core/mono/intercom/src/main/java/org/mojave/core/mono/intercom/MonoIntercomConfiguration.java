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

import org.mojave.component.openapi.OpenApiConfiguration;
import org.mojave.component.web.error.RestErrorConfiguration;
import org.mojave.component.web.spring.mvc.JsonWebMvcConfigurationSupport;
import org.mojave.component.web.spring.security.SpringSecurityConfiguration;
import org.mojave.scheme.common.datatype.DatatypeConfiguration;
import org.mojave.core.participant.domain.ParticipantDomainConfiguration;
import org.mojave.core.participant.intercom.ParticipantIntercomConfiguration;
import org.mojave.core.wallet.domain.WalletDomainConfiguration;
import org.mojave.core.wallet.intercom.WalletIntercomConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tools.jackson.databind.ObjectMapper;

@EnableWebMvc
@EnableAsync
@ComponentScan(
    basePackages = {"org.mojave.mono.intercom.controller"})
@Import(
    value = {
        OpenApiConfiguration.class,
        DatatypeConfiguration.class,
        RestErrorConfiguration.class,
        SpringSecurityConfiguration.class,
        ParticipantIntercomConfiguration.class,
        WalletIntercomConfiguration.class})
public class MonoIntercomConfiguration extends JsonWebMvcConfigurationSupport {

    public MonoIntercomConfiguration(ObjectMapper objectMapper) {

        super(objectMapper);
    }

    public interface RequiredDependencies extends OpenApiConfiguration.RequiredBeans,
                                                  SpringSecurityConfiguration.RequiredBeans,
                                                  ParticipantIntercomConfiguration.RequiredDependencies,
                                                  WalletIntercomConfiguration.RequiredDependencies { }

    public interface RequiredSettings extends OpenApiConfiguration.RequiredSettings,
                                              SpringSecurityConfiguration.RequiredSettings,
                                              ParticipantDomainConfiguration.RequiredSettings,
                                              WalletDomainConfiguration.RequiredSettings {

        TomcatSettings tomcatSettings();

    }

    public record TomcatSettings(int portNo) { }

}
