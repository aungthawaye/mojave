package io.mojaloop.common.component.tomcat.connector;

import io.mojaloop.common.component.tomcat.ConnectorDecorator;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;

public class SimpleHttpConnectorDecorator implements ConnectorDecorator {

    private final Settings settings;

    public SimpleHttpConnectorDecorator(Settings settings) {

        assert settings != null;

        this.settings = settings;
    }

    @Override
    public void decorate(Connector connector) {

        if (connector.getProtocolHandler() instanceof Http11NioProtocol protocol) {

            connector.setPort(this.settings.port());
            connector.setSecure(false);
            connector.setScheme("http");

            protocol.setMaxThreads(this.getMaxThreads());
            protocol.setConnectionTimeout(this.getConnectionTimeout());
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

    public record Settings(int port, int maxThreads, int connectionTimeout) { }

}
