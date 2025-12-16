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

package org.mojave.mono.admin;

import org.mojave.component.openapi.OpenApiConfiguration;
import org.mojave.component.web.spring.mvc.JsonWebMvcConfigurationSupport;
import org.mojave.component.web.spring.security.SpringSecurityConfiguration;
import org.mojave.core.accounting.admin.AccountingAdminConfiguration;
import org.mojave.core.accounting.domain.AccountingDomainConfiguration;
import org.mojave.core.participant.admin.ParticipantAdminConfiguration;
import org.mojave.core.participant.domain.ParticipantDomainConfiguration;
import org.mojave.core.wallet.admin.WalletAdminConfiguration;
import org.mojave.core.wallet.domain.WalletDomainConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tools.jackson.databind.ObjectMapper;

@EnableWebMvc
@EnableAsync
@ComponentScan(
    basePackages = {"org.mojave.mono.admin.controller"})
@Import(
    value = {
        OpenApiConfiguration.class,
        SpringSecurityConfiguration.class,
        ParticipantAdminConfiguration.class,
        AccountingAdminConfiguration.class,
        WalletAdminConfiguration.class})
public class MonoAdminConfiguration extends JsonWebMvcConfigurationSupport {

    public MonoAdminConfiguration(ObjectMapper objectMapper) {

        super(objectMapper);
    }

    public interface RequiredDependencies extends OpenApiConfiguration.RequiredBeans,
                                                  SpringSecurityConfiguration.RequiredBeans,
                                                  ParticipantAdminConfiguration.RequiredDependencies,
                                                  AccountingAdminConfiguration.RequiredDependencies,
                                                  WalletAdminConfiguration.RequiredDependencies { }

    public interface RequiredSettings extends OpenApiConfiguration.RequiredSettings,
                                              SpringSecurityConfiguration.RequiredSettings,
                                              ParticipantDomainConfiguration.RequiredSettings,
                                              AccountingDomainConfiguration.RequiredSettings,
                                              WalletDomainConfiguration.RequiredSettings {

        TomcatSettings tomcatSettings();

    }

    public record TomcatSettings(int portNo) { }

}
