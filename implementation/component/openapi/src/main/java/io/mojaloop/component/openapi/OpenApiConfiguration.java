/*-
 * ================================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
package io.mojaloop.component.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;

public class OpenApiConfiguration {

    @Bean
    public OpenAPI mojaveOpenAPI(ApiSettings apiSettings) {

        return new OpenAPI().info(new Info().title(apiSettings.title()).version(apiSettings.version()));
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

    public interface RequiredSettings {

        OpenApiConfiguration.ApiSettings apiSettings();

    }

    public record ApiSettings(String title, String version) { }

}
