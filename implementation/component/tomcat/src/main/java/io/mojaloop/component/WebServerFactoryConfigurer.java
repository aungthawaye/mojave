package io.mojaloop.component;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

import java.nio.file.Path;

public class WebServerFactoryConfigurer
    implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Override
    public void customize(ConfigurableWebServerFactory factory) {

    }

    public record ServerSettings(int port,
                                 int maxThreads,
                                 int minSpareThreads,
                                 int maxConnections,
                                 int connectionTimeoutMillis,
                                 Path keyStoreB64Path,
                                 String keyStorePassword,
                                 Path trustStoreB64Path,
                                 String trustStorePassword) { }

}
