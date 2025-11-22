package io.mojaloop.mono.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@Import(value = {MonoAdminConfiguration.class, MonoAdminSettings.class})
public class MonoAdminApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonoAdminApplication.class);

    public static void main(String[] args) {

        new SpringApplicationBuilder(MonoAdminApplication.class).web(WebApplicationType.SERVLET)
                                                                .properties("spring.application.name=mono-admin", "spring.jmx.enabled=true", "spring.jmx.unique-types=true",
                                                                    "spring.jmx.default-domain=mono-admin", "spring.application.admin.enabled=true",
                                                                    "management.endpoints.web.base-path=/actuator", "management.endpoint.health.show-details=always",
                                                                    "management.endpoint.health.group.readiness.include=db,diskSpace,process,throttling",
                                                                    "management.endpoint.health.group.liveness.include=db,diskSpace,process,throttling",
                                                                    "management.endpoint.health.group.throttling.include=throttling", "management.endpoint.throttling.enabled=true",
                                                                    "management.endpoint.health.validate-group-membership=false", "management.endpoint.health.probes.enabled=true",
                                                                    "management.endpoints.web.exposure.include=health,info,metrics,prometheus",
                                                                    "management.endpoint.health.show-details=always",
                                                                    "spring.application.admin.jmx-name=org.springframework.boot:type=Admin,name=MonoAdminApplication,context=mono-admin")
                                                                .run(args);
    }

}
