package io.mojaloop.connector.service.outbound;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = "io.mojaloop.connector.service.outbound")
@Import({ConnectorOutboundConfiguration.class})
public class ConnectorOutboundApplication { }
