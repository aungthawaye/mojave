package io.mojaloop.connector.sample;

import io.mojaloop.component.misc.spring.SpringLauncher;
import io.mojaloop.connector.gateway.ConnectorGatewayApplication;

public class SampleConnectorApplication {

    public static void main(String[] args) {

        SpringLauncher.launch((s) -> ConnectorGatewayApplication.run(s, SampleConnectorConfiguration.class, SampleConnectorSettings.class), args);
    }

}
