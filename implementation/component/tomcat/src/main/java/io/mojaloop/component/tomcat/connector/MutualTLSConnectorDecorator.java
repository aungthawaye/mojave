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

package io.mojaloop.component.tomcat.connector;

import io.mojaloop.component.misc.handy.P12Reader;
import io.mojaloop.component.tomcat.ConnectorDecorator;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.security.KeyStoreUtil;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class MutualTLSConnectorDecorator implements ConnectorDecorator {

    private final Settings settings;

    public MutualTLSConnectorDecorator(Settings settings) {

        assert settings != null;

        this.settings = settings;
    }

    @Override
    public void decorate(Connector connector) {

        var protocolHandler = connector.getProtocolHandler();

        if (protocolHandler instanceof Http11NioProtocol protocol) {

            connector.setPort(this.settings.port());
            connector.setSecure(true);
            connector.setScheme("https");

            protocol.setMaxThreads(this.getMaxThreads());
            protocol.setConnectionTimeout(this.getConnectionTimeout());
            protocol.setSSLEnabled(true);

            var sslHostConfig = new SSLHostConfig();
            sslHostConfig.setCertificateVerification("required");

            for (var keyStoreSettings : settings.keyStoreSettings()) {
                this.addKeyStore(sslHostConfig, keyStoreSettings);
            }

            if (settings.trustStoreSettings() != null) {
                this.setTrustStore(sslHostConfig, settings.trustStoreSettings());
            }

            protocol.addSslHostConfig(sslHostConfig);
        }
    }

    @Override
    public int getConnectionTimeout() {

        return this.settings.connectionTimeout();
    }

    @Override
    public int getMaxThreads() {

        return this.settings.maxThreads();
    }

    @Override
    public int getPort() {

        return this.settings.port();
    }

    private void addKeyStore(SSLHostConfig sslHostConfig, Settings.KeyStoreSettings keyStoreSettings) {

        try (var in = P12Reader.read(keyStoreSettings.contentType(), keyStoreSettings.contentValue())) {

            var keyStore = KeyStore.getInstance("PKCS12");
            KeyStoreUtil.load(keyStore, in, keyStoreSettings.storePassword().toCharArray());

            var certificate = new SSLHostConfigCertificate(sslHostConfig, SSLHostConfigCertificate.Type.RSA);

            certificate.setCertificateKeystore(keyStore);

            if (keyStoreSettings.keyAlias() != null && !keyStoreSettings.keyAlias().isBlank()) {

                certificate.setCertificateKeyAlias(keyStoreSettings.keyAlias());
            }

            sslHostConfig.addCertificate(certificate);

        } catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void setTrustStore(SSLHostConfig sslHostConfig, Settings.TrustStoreSettings trustStoreSettings) {

        try (var in = P12Reader.read(trustStoreSettings.contentType(), trustStoreSettings.contentValue())) {

            var keyStore = KeyStore.getInstance("PKCS12");
            KeyStoreUtil.load(keyStore, in, trustStoreSettings.storePassword().toCharArray());

            sslHostConfig.setTrustStore(keyStore);

        } catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public record Settings(int port, int maxThreads, int connectionTimeout, TrustStoreSettings trustStoreSettings, KeyStoreSettings... keyStoreSettings) {

        public record KeyStoreSettings(P12Reader.ContentType contentType, String contentValue, String storePassword, String keyAlias) {

        }

        public record TrustStoreSettings(P12Reader.ContentType contentType, String contentValue, String storePassword) { }

    }

}
