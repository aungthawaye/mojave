package io.mojaloop.connector.service.inbound;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootConfiguration
@EnableAutoConfiguration
@Import(ConnectorInboundConfiguration.class)
public class ConnectorInboundApplication { }
