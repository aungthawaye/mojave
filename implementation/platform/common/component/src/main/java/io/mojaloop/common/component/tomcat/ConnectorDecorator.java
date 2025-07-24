package io.mojaloop.common.component.tomcat;

import org.apache.catalina.connector.Connector;

public interface ConnectorDecorator {

    void decorate(Connector connector);

    int getConnectionTimeout();

    int getMaxThreads();

    int getPort();

}
