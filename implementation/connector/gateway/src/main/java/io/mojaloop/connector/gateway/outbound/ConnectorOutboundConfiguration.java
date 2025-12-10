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

package io.mojaloop.connector.gateway.outbound;

import tools.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.component.openapi.OpenApiConfiguration;
import io.mojaloop.component.web.spring.security.AuthenticationErrorWriter;
import io.mojaloop.component.web.spring.security.Authenticator;
import io.mojaloop.component.web.spring.security.SpringSecurityConfiguration;
import io.mojaloop.component.web.spring.security.SpringSecurityConfigurer;
import io.mojaloop.connector.gateway.outbound.component.FspiopOutboundGatekeeper;
import io.mojaloop.fspiop.invoker.FspiopInvokerConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableAutoConfiguration(exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableAsync
@EnableWebMvc
@Import(
    value = {
        MiscConfiguration.class,
        FspiopInvokerConfiguration.class,
        OpenApiConfiguration.class,
        SpringSecurityConfiguration.class})
@ComponentScan(basePackages = {"io.mojaloop.connector.gateway.outbound"})
public class ConnectorOutboundConfiguration implements MiscConfiguration.RequiredBeans,
                                                       FspiopInvokerConfiguration.RequiredBeans,
                                                       SpringSecurityConfiguration.RequiredBeans,
                                                       OpenApiConfiguration.RequiredSettings,
                                                       SpringSecurityConfiguration.RequiredSettings {

    private final OutboundSettings outboundSettings;

    private final ObjectMapper objectMapper;

    public ConnectorOutboundConfiguration(OutboundSettings outboundSettings,
                                          ObjectMapper objectMapper) {

        assert outboundSettings != null;
        assert objectMapper != null;

        this.outboundSettings = outboundSettings;
        this.objectMapper = objectMapper;

    }

    @Bean
    @Override
    public OpenApiConfiguration.ApiSettings apiSettings() {

        return new OpenApiConfiguration.ApiSettings("Connector - Outbound API", "1.0.0");
    }

    @Bean
    @Override
    public AuthenticationErrorWriter authenticationErrorWriter() {

        return new FspiopOutboundGatekeeper.ErrorWriter(this.objectMapper);
    }

    @Bean
    @Override
    public Authenticator authenticator() {

        return new FspiopOutboundGatekeeper(this.outboundSettings);
    }

    @Bean
    @Override
    public SpringSecurityConfigurer.Settings springSecuritySettings() {

        return new SpringSecurityConfigurer.Settings(new String[]{
            "/lookup",
            "/quote",
            "/transfer"});
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> webServerFactoryCustomizer(
        OutboundSettings outboundSettings) {

        return factory -> {

            factory.setPort(outboundSettings.portNo());

            factory.addConnectorCustomizers(connector -> {
                var protocol = (org.apache.coyote.http11.AbstractHttp11Protocol<?>) connector.getProtocolHandler();
                protocol.setConnectionTimeout(outboundSettings.connectionTimeoutMs());
                protocol.setMaxThreads(outboundSettings.maxThreads());
            });
        };
    }

    public interface RequiredBeans {

        PubSubClient pubSubClient();

    }

    public interface RequiredSettings
        extends MiscConfiguration.RequiredSettings, FspiopInvokerConfiguration.RequiredSettings {

        OutboundSettings outboundSettings();

        TransferSettings transferSettings();

    }

    public record OutboundSettings(int portNo,
                                   int maxThreads,
                                   int connectionTimeoutMs,
                                   int putResultTimeoutMs,
                                   int pubSubTimeoutMs,
                                   String publicKeyPem,
                                   boolean secured) { }

    public record TransferSettings(int transferRequestExpirySeconds) { }

}
