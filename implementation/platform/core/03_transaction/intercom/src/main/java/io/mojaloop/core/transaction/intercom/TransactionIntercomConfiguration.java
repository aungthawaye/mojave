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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.web.error.RestErrorConfiguration;
import io.mojaloop.component.web.spring.mvc.JacksonWebMvcExtension;
import io.mojaloop.component.web.spring.security.AuthenticationErrorWriter;
import io.mojaloop.component.web.spring.security.Authenticator;
import io.mojaloop.component.web.spring.security.SpringSecurityConfiguration;
import io.mojaloop.component.web.spring.security.SpringSecurityConfigurer;
import io.mojaloop.core.transaction.domain.TransactionDomainConfiguration;
import io.mojaloop.core.transaction.intercom.component.EmptyErrorWriter;
import io.mojaloop.core.transaction.intercom.component.EmptyGatekeeper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableAutoConfiguration
@Configuration(proxyBeanMethods = false)
@EnableWebMvc
@EnableAsync
@ComponentScan(basePackages = "io.mojaloop.core.transaction.intercom")
@Import(value = {TransactionDomainConfiguration.class, RestErrorConfiguration.class, SpringSecurityConfiguration.class})
public class TransactionIntercomConfiguration extends JacksonWebMvcExtension
    implements TransactionDomainConfiguration.RequiredBeans, SpringSecurityConfiguration.RequiredBeans, SpringSecurityConfiguration.RequiredSettings {

    public TransactionIntercomConfiguration(ObjectMapper objectMapper) {

        super(objectMapper);
    }

    @Bean
    @Override
    public AuthenticationErrorWriter authenticationErrorWriter() {

        return new EmptyErrorWriter();
    }

    @Bean
    @Override
    public Authenticator authenticator() {

        return new EmptyGatekeeper();
    }

    @Bean
    @Override
    public SpringSecurityConfigurer.Settings springSecuritySettings() {

        return new SpringSecurityConfigurer.Settings(null);
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer(TomcatSettings settings) {

        return factory -> factory.setPort(settings.portNo());
    }

    public interface RequiredSettings extends TransactionDomainConfiguration.RequiredSettings {

        TomcatSettings tomcatSettings();

    }

    public record TomcatSettings(int portNo) { }

}
