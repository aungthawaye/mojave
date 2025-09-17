package io.mojaloop.connector.gateway;

import io.mojaloop.connector.gateway.inbound.ConnectorInboundConfiguration;
import io.mojaloop.connector.gateway.outbound.ConnectorOutboundConfiguration;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;


public class ConnectorGatewayApplication {

    public static ConfigurableApplicationContext run(String[] args, Class<?>... extraConfigurations) {

        var coreConfigurations = new Class[]{ConnectorGatewayConfiguration.class};
        var fullConfigurations = new Class[extraConfigurations.length + coreConfigurations.length];

        if (extraConfigurations.length > 0) {
            System.arraycopy(coreConfigurations, 0, fullConfigurations, 0, coreConfigurations.length);
            System.arraycopy(extraConfigurations, 0, fullConfigurations, coreConfigurations.length, extraConfigurations.length);
        }

        return new SpringApplicationBuilder(fullConfigurations)
                      .web(WebApplicationType.NONE)
                      .properties("spring.profiles.active=prod")
                      .child(ConnectorInboundConfiguration.class)
                      .properties("spring.application.name=connector-inbound", "spring.jmx.enabled=true", "spring.jmx.unique-names=true",
                                  "spring.jmx.default-domain=connector-inbound", "spring.application.admin.enabled=true",
                                  "management.endpoints.web.base-path=/actuator",
                                  "management.endpoints.web.exposure.include=health,info,metrics,prometheus",
                                  "spring.application.admin.jmx-name=org.springframework.boot:type=Admin,name=Inbound,context=connector-inbound")
                      .web(WebApplicationType.SERVLET)
                      .sibling(ConnectorOutboundConfiguration.class)
                      .properties("spring.application.name=connector-outbound", "spring.jmx.enabled=true", "spring.jmx.unique-names=true",
                                  "spring.jmx.default-domain=connector-outbound", "spring.application.admin.enabled=true",
                                  "management.endpoints.web.base-path=/actuator",
                                  "management.endpoints.web.exposure.include=health,info,metrics,prometheus",
                                  "spring.application.admin.jmx-name=org.springframework.boot:type=Admin,name=Outbound,context=connector-outbound")
                      .web(WebApplicationType.SERVLET)
                      .run(args);
    }

}
