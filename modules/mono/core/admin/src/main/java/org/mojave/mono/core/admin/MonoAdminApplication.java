package org.mojave.mono.core.admin;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration(
    exclude = {
        SecurityAutoConfiguration.class,
        JacksonAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class})
@Import(
    value = {
        MonoAdminConfiguration.class,
        MonoAdminDependencies.class,
        MonoAdminSettings.class})
public class MonoAdminApplication {

    static void main(String[] args) {

        new SpringApplicationBuilder(MonoAdminApplication.class)
            .web(WebApplicationType.SERVLET)
            .properties(
                "spring.application.name=MonoAdminApplication",
                "management.endpoints.web.base-path=/actuator",
                "management.endpoint.health.show-details=always",
                "management.endpoint.health.group.readiness.include=db,diskSpace,process,throttling",
                "management.endpoint.health.group.liveness.include=db,diskSpace,process,throttling",
                "management.endpoint.health.group.throttling.include=throttling",
                "management.endpoint.throttling.enabled=true",
                "management.endpoint.health.validate-group-membership=false",
                "management.endpoint.health.probes.enabled=true",
                "management.endpoints.web.exposure.include=health,info,metrics,prometheus",
                "management.endpoint.health.show-details=always")
            .run(args);
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer(
        MonoAdminConfiguration.TomcatSettings settings) {

        return factory -> factory.setPort(settings.portNo());
    }

}
