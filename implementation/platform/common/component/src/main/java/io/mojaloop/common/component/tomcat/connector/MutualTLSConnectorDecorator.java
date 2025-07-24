package io.mojaloop.common.component.tomcat.connector;

import io.mojaloop.common.component.tomcat.ConnectorDecorator;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.security.KeyStoreUtil;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Base64;

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

        try (var in = this.openKeyStoreStream(keyStoreSettings.contentType(), keyStoreSettings.contentValue())) {

            var keyStore = KeyStore.getInstance("PKCS12");
            KeyStoreUtil.load(keyStore, in, keyStoreSettings.storePassword().toCharArray());

            var certificate = new SSLHostConfigCertificate(sslHostConfig, SSLHostConfigCertificate.Type.RSA);

            certificate.setCertificateKeystore(keyStore);
            certificate.setCertificateKeyAlias(keyStoreSettings.keyAlias());
            certificate.setCertificateKeyPassword(keyStoreSettings.keyPassword());

            sslHostConfig.addCertificate(certificate);

        } catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream openKeyStoreStream(Settings.ContentType contentType, String contentValue) throws IOException {

        return switch (contentType) {
            case P12_FILE_CLASS_PATH -> new ClassPathResource(contentValue).getInputStream();
            case P12_FILE_ABSOLUTE_PATH -> Files.newInputStream(Paths.get(contentValue));
            case P12_CONTENT_IN_BASE64 -> new ByteArrayInputStream(Base64.getMimeDecoder().decode(contentValue.getBytes()));
        };
    }

    private void setTrustStore(SSLHostConfig sslHostConfig, Settings.TrustStoreSettings trustStoreSettings) {

        try (var in = this.openKeyStoreStream(trustStoreSettings.contentType(), trustStoreSettings.contentValue())) {

            var keyStore = KeyStore.getInstance("PKCS12");
            KeyStoreUtil.load(keyStore, in, trustStoreSettings.storePassword().toCharArray());

            sslHostConfig.setTrustStore(keyStore);

        } catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public record Settings(int port,
                           int maxThreads,
                           int connectionTimeout,
                           TrustStoreSettings trustStoreSettings,
                           KeyStoreSettings... keyStoreSettings) {

        public enum ContentType {
            P12_FILE_CLASS_PATH,
            P12_FILE_ABSOLUTE_PATH,
            P12_CONTENT_IN_BASE64
        }

        public record KeyStoreSettings(ContentType contentType,
                                       String contentValue,
                                       String storePassword,
                                       String keyPassword,
                                       String keyAlias) {

        }

        public record TrustStoreSettings(ContentType contentType, String contentValue, String storePassword) { }

    }

}
