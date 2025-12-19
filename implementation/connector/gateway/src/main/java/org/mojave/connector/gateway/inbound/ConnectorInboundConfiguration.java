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

package org.mojave.connector.gateway.inbound;

import org.mojave.component.misc.MiscConfiguration;
import org.mojave.component.misc.crypto.KeyStores;
import org.mojave.component.misc.pubsub.PubSubClient;
import org.mojave.component.tomcat.MutualTlsTomcatFactoryConfigurer;
import org.mojave.component.tomcat.SimpleTomcatFactoryConfigurer;
import org.mojave.component.web.spring.security.AuthenticationErrorWriter;
import org.mojave.component.web.spring.security.Authenticator;
import org.mojave.component.web.spring.security.SpringSecurityConfiguration;
import org.mojave.component.web.spring.security.SpringSecurityConfigurer;
import org.mojave.connector.adapter.ConnectorAdapterConfiguration;
import org.mojave.connector.gateway.inbound.component.FspiopInboundGatekeeper;
import org.mojave.fspiop.component.participant.ParticipantContext;
import org.mojave.fspiop.invoker.FspiopInvokerConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

@EnableAutoConfiguration(exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableAsync
@EnableWebMvc
@Import(
    value = {
        MiscConfiguration.class,
        ConnectorAdapterConfiguration.class,
        FspiopInvokerConfiguration.class,
        SpringSecurityConfiguration.class,})
@ComponentScan(basePackages = {"org.mojave.connector.gateway.inbound"})
public class ConnectorInboundConfiguration implements MiscConfiguration.RequiredBeans,
                                                      FspiopInvokerConfiguration.RequiredBeans,
                                                      SpringSecurityConfiguration.RequiredBeans,
                                                      SpringSecurityConfiguration.RequiredSettings {

    private final ParticipantContext participantContext;

    private final ObjectMapper objectMapper;

    public ConnectorInboundConfiguration(ParticipantContext participantContext,
                                         ObjectMapper objectMapper) {

        assert participantContext != null;
        assert objectMapper != null;

        this.participantContext = participantContext;
        this.objectMapper = objectMapper;

    }

    @Bean
    @Override
    public AuthenticationErrorWriter authenticationErrorWriter() {

        return new FspiopInboundGatekeeper.ErrorWriter(this.objectMapper);
    }

    @Bean
    @Override
    public Authenticator authenticator() {

        return new FspiopInboundGatekeeper(this.participantContext, this.objectMapper);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));  // or your allowed origins
        config.setAllowedMethods(
            List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;

    }

    @Bean
    @Override
    public SpringSecurityConfigurer.Settings springSecuritySettings() {

        return new SpringSecurityConfigurer.Settings(new String[]{
            "/parties/**",
            "/quotes/**",
            "/transfers/**"});
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> webServerFactoryCustomizer(
        InboundSettings inboundSettings)
        throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException {

        if (!inboundSettings.useMutualTls()) {

            return SimpleTomcatFactoryConfigurer.configure(
                new SimpleTomcatFactoryConfigurer.ServerSettings(
                    inboundSettings.portNo(), inboundSettings.maxThreads(),
                    inboundSettings.maxThreads() / 2, inboundSettings.maxThreads(),
                    inboundSettings.connectionTimeout(), inboundSettings.maxThreads(), 20_000));
        } else {

            var keyStore = KeyStores.Base64Pkcs12.base64(
                inboundSettings.keyStoreSettings.p12b64Content,
                inboundSettings.keyStoreSettings.password);

            var trustStore = KeyStores.Base64Pkcs12.base64(
                inboundSettings.trustStoreSettings.p12b64Content,
                inboundSettings.trustStoreSettings.password);

            return MutualTlsTomcatFactoryConfigurer.configure(
                new MutualTlsTomcatFactoryConfigurer.ServerSettings(
                    inboundSettings.portNo(), inboundSettings.maxThreads(),
                    inboundSettings.maxThreads() / 2, inboundSettings.maxThreads(),
                    inboundSettings.connectionTimeout(), inboundSettings.maxThreads(), 20_000),
                keyStore, inboundSettings.keyStoreSettings.password, trustStore);
        }
    }

    public interface RequiredBeans extends ConnectorAdapterConfiguration.RequiredBeans {

        PubSubClient pubSubClient();

    }

    public interface RequiredSettings extends MiscConfiguration.RequiredSettings,
                                              ConnectorAdapterConfiguration.RequiredSettings,
                                              FspiopInvokerConfiguration.RequiredSettings {

        InboundSettings inboundSettings();

    }

    public record InboundSettings(int portNo,
                                  int maxThreads,
                                  int connectionTimeout,
                                  boolean useMutualTls,
                                  KeyStoreSettings keyStoreSettings,
                                  TrustStoreSettings trustStoreSettings) {

        public record KeyStoreSettings(String p12b64Content, String password, String keyAlias) { }

        public record TrustStoreSettings(String p12b64Content, String password) { }

    }

}
