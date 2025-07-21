package io.mojaloop.common.component.tomcat;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.security.KeyStoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class TomcatMutualTLSConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TomcatMutualTLSConfigurer.class);

    private static void addKeyStore(SSLHostConfig sslHostConfig, Settings.KeyStoreSettings keyStoreSettings) {

        try (var in = openKeyStoreStream(keyStoreSettings.contentType(), keyStoreSettings.contentValue())) {

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

    public static void configure(Connector connector, Settings settings) {

        var protocolHandler = connector.getProtocolHandler();

        if (protocolHandler instanceof Http11NioProtocol protocol) {

            connector.setPort(settings.port());
            connector.setSecure(true);
            connector.setScheme("https");

            protocol.setSSLEnabled(true);

            var sslHostConfig = new SSLHostConfig();
            sslHostConfig.setCertificateVerification("required");

            for (var keyStoreSettings : settings.keyStoreSettings()) {
                addKeyStore(sslHostConfig, keyStoreSettings);
            }

            if (settings.trustStoreSettings() != null) {
                setTrustStore(sslHostConfig, settings.trustStoreSettings());
            }

            protocol.addSslHostConfig(sslHostConfig);
        }
    }

    private static InputStream openKeyStoreStream(Settings.ContentType contentType, String contentValue) throws IOException {

        return switch (contentType) {
            case P12_CLASS_PATH -> new ClassPathResource(contentValue).getInputStream();
            case P12_FILE_PATH -> Files.newInputStream(Paths.get(contentValue));
            case P12_BASE64_CONTENT -> new ByteArrayInputStream(Base64.getMimeDecoder().decode(contentValue.getBytes()));
        };
    }

    private static void setTrustStore(SSLHostConfig sslHostConfig, Settings.TrustStoreSettings trustStoreSettings) {

        try (var in = openKeyStoreStream(trustStoreSettings.contentType(), trustStoreSettings.contentValue())) {

            var keyStore = KeyStore.getInstance("PKCS12");
            KeyStoreUtil.load(keyStore, in, trustStoreSettings.storePassword().toCharArray());

            sslHostConfig.setTrustStore(keyStore);

        } catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public record Settings(int port, TrustStoreSettings trustStoreSettings, KeyStoreSettings... keyStoreSettings) {

        public enum ContentType {
            P12_CLASS_PATH,
            P12_FILE_PATH,
            P12_BASE64_CONTENT
        }

        public record KeyStoreSettings(ContentType contentType,
                                       String contentValue,
                                       String storePassword,
                                       String keyPassword,
                                       String keyAlias) {

        }

        public record TrustStoreSettings(ContentType contentType, String contentValue, String storePassword) {

        }

    }

}