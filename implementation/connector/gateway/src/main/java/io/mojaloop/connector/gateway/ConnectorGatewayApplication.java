package io.mojaloop.connector.gateway;

import io.mojaloop.connector.gateway.inbound.ConnectorInboundApplication;
import io.mojaloop.connector.gateway.outbound.ConnectorOutboundApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConnectorGatewayApplication {

    public static void run(String[] args, Class<?>... configurations) {

        var sources = new Class[configurations.length + 1];

        sources[0] = ConnectorGatewayApplication.class;

        if (configurations.length > 0) {
            System.arraycopy(configurations, 0, sources, 1, configurations.length);
        }

        new SpringApplicationBuilder(sources)
            .web(WebApplicationType.NONE)
            .properties("spring.profiles.active=prod")
            .child(ConnectorInboundApplication.class)
            .properties("spring.application.name=connector-inbound", "spring.jmx.enabled=true", "spring.jmx.unique-names=true",
                        "spring.jmx.default-domain=connector-inbound", "spring.application.admin.enabled=true",
                        "management.endpoints.web.base-path=/actuator",
                        "management.endpoints.web.exposure.include=health,info,metrics,prometheus",
                        "spring.application.admin.jmx-name=org.springframework.boot:type=Admin,name=Inbound,context=connector-inbound")
            .web(WebApplicationType.SERVLET)
            .sibling(ConnectorOutboundApplication.class)
            .properties("spring.application.name=connector-outbound", "spring.jmx.enabled=true", "spring.jmx.unique-names=true",
                        "spring.jmx.default-domain=connector-outbound", "spring.application.admin.enabled=true",
                        "management.endpoints.web.base-path=/actuator",
                        "management.endpoints.web.exposure.include=health,info,metrics,prometheus",
                        "spring.application.admin.jmx-name=org.springframework.boot:type=Admin,name=Outbound,context=connector-outbound")
            .web(WebApplicationType.SERVLET)
            .run(args);
    }

}
