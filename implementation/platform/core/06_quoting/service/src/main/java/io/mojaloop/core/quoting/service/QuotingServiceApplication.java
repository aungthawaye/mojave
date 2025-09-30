package io.mojaloop.core.quoting.service;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@Import(value = {QuotingServiceConfiguration.class, QuotingServiceSettings.class})
public class QuotingServiceApplication {

    public static void main(String[] args) {

        new SpringApplicationBuilder(QuotingServiceApplication.class)
            .web(WebApplicationType.SERVLET)
            .properties(
                "spring.application.name=quoting-service",
                "spring.jmx.enabled=true",
                "spring.jmx.unique-names=true",
                "spring.jmx.default-domain=quoting-service",
                "spring.application.admin.enabled=true",
                "management.endpoints.web.base-path=/actuator",
                "management.endpoint.health.show-details=always",
                "management.endpoint.health.group.readiness.include=db,diskSpace,process,throttling",
                "management.endpoint.health.group.liveness.include=db,diskSpace,process,throttling",
                "management.endpoint.health.group.throttling.include=throttling",
                "management.endpoint.throttling.enabled=true",
                "management.endpoint.health.validate-group-membership=false",
                "management.endpoint.health.probes.enabled=true",
                "management.endpoints.web.exposure.include=health,info,metrics,prometheus",
                "management.endpoint.health.show-details=always",
                "spring.application.admin.jmx-name=org.springframework.boot:type=Admin,name=QuotingServiceApplication,context=quoting-service")
            .run(args);
    }

}