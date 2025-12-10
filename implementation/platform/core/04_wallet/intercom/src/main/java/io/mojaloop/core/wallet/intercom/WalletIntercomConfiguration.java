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

package io.mojaloop.core.wallet.intercom;
import io.mojaloop.component.openapi.OpenApiConfiguration;
import io.mojaloop.component.web.error.RestErrorConfiguration;
import io.mojaloop.component.web.logging.RequestIdMdcConfiguration;
import io.mojaloop.component.web.spring.mvc.WebMvcExtension;
import io.mojaloop.component.web.spring.security.SpringSecurityConfiguration;
import io.mojaloop.core.common.datatype.DatatypeConfiguration;
import io.mojaloop.core.wallet.domain.WalletDomainConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableAutoConfiguration

@EnableWebMvc
@EnableAsync
@ComponentScan(basePackages = "io.mojaloop.core.wallet.intercom.controller")
@Import(
    value = {
        OpenApiConfiguration.class,
        DatatypeConfiguration.class,
        RequestIdMdcConfiguration.class,
        WalletDomainConfiguration.class,
        RestErrorConfiguration.class,
        SpringSecurityConfiguration.class})
public final class WalletIntercomConfiguration extends WebMvcExtension {

    public WalletIntercomConfiguration(tools.jackson.databind.json.JsonMapper jsonMapper) {

        super(jsonMapper);
    }

    public interface RequiredDependencies extends WalletDomainConfiguration.RequiredBeans,
                                                  SpringSecurityConfiguration.RequiredBeans { }

    public interface RequiredSettings extends WalletDomainConfiguration.RequiredSettings,
                                              OpenApiConfiguration.RequiredSettings,
                                              SpringSecurityConfiguration.RequiredSettings {

        TomcatSettings tomcatSettings();

    }

    public record TomcatSettings(int portNo) { }

}
