package io.mojaloop.connector.gateway.outbound;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = "io.mojaloop.connector.gateway.outbound")
@Import({ConnectorOutboundConfiguration.class, ConnectorOutboundSettings.class})
public class ConnectorOutboundApplication { }
