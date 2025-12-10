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

package org.mojave.core.transaction.intercom;
import org.mojave.component.openapi.OpenApiConfiguration;
import org.mojave.component.web.error.RestErrorConfiguration;
import org.mojave.component.web.logging.RequestIdMdcConfiguration;
import org.mojave.component.web.spring.mvc.WebMvcExtension;
import org.mojave.component.web.spring.security.SpringSecurityConfiguration;
import org.mojave.core.common.datatype.DatatypeConfiguration;
import org.mojave.core.transaction.domain.TransactionDomainConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableAutoConfiguration

@EnableWebMvc
@EnableAsync
@ComponentScan(basePackages = "org.mojave.core.transaction.intercom.controller")
@Import(
    value = {
        OpenApiConfiguration.class,
        DatatypeConfiguration.class,
        RequestIdMdcConfiguration.class,
        TransactionDomainConfiguration.class,
        RestErrorConfiguration.class,
        SpringSecurityConfiguration.class})
public final class TransactionIntercomConfiguration extends WebMvcExtension {

    public TransactionIntercomConfiguration(tools.jackson.databind.json.JsonMapper jsonMapper) {

        super(jsonMapper);
    }

    public interface RequiredDependencies extends TransactionDomainConfiguration.RequiredBeans,
                                                  SpringSecurityConfiguration.RequiredBeans { }

    public interface RequiredSettings extends TransactionDomainConfiguration.RequiredSettings,
                                              SpringSecurityConfiguration.RequiredSettings,
                                              OpenApiConfiguration.RequiredSettings {

        TomcatSettings tomcatSettings();

    }

    public record TomcatSettings(int portNo) { }

}
