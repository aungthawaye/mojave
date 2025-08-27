package io.mojaloop.connector.sample;

import io.mojaloop.connector.gateway.ConnectorGatewayApplication;

public class SampleConnectorApplication {

    public static void main(String[] args) {

        ConnectorGatewayApplication.run(args, SampleConnectorConfiguration.class, SampleConnectorSettings.class);
    }

}
