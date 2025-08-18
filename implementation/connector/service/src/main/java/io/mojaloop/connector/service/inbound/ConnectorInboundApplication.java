package io.mojaloop.connector.service.inbound;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = "io.mojaloop.connector.service.inbound")
@Import(ConnectorInboundConfiguration.class)
public class ConnectorInboundApplication { }
