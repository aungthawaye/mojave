package io.mojaloop.core.participant.admin;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI mojaveOpenAPI() {

        return new OpenAPI().info(new Info().title("Mojave - Participant - Admin").version("1.0.0"));
    }

    @Bean
    public OperationCustomizer operationNameCustomizer() {

        return (operation, handlerMethod) -> {
            Class<?> controllerClass = handlerMethod.getBeanType();
            String className = controllerClass.getSimpleName(); // e.g. CreateFsp, CreateOracle

            if (operation.getSummary() == null || operation.getSummary().isBlank()) {
                operation.setSummary(className);
            }

            if (operation.getOperationId() == null || operation.getOperationId().startsWith("execute")) {
                operation.setOperationId(className + "_" + handlerMethod.getMethod().getName());
            }

            return operation;
        };
    }

}