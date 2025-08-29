package io.mojaloop.connector.gateway.inbound;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = "io.mojaloop.connector.gateway.inbound")
@Import({ConnectorInboundConfiguration.class, ConnectorInboundSettings.class})
public class ConnectorInboundApplication { }
