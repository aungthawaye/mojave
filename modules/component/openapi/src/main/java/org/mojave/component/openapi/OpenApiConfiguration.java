/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */

package org.mojave.component.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class OpenApiConfiguration {

    static {
        SpringDocUtils
            .getConfig()
            .replaceWithSchema(
                Instant.class,
                new StringSchema().example("epoch seconds or ISO-8601"))
            .replaceWithSchema(BigDecimal.class, new StringSchema().example("decimal"))
            .replaceWithSchema(Long.class, new StringSchema().example("integer"))
            .replaceWithSchema(Integer.class, new StringSchema().example("integer"))
            .replaceWithSchema(Boolean.class, new StringSchema().example("true or false"));
    }

    @Bean
    public OpenAPI mojaveOpenAPI(ApiSettings apiSettings) {

        return new OpenAPI().info(
            new Info().title(apiSettings.title()).version(apiSettings.version()));
    }

    @Bean
    public OperationCustomizer operationNameCustomizer() {

        return (operation, handlerMethod) -> {

            final var controllerClass = handlerMethod.getBeanType();
            final var className = controllerClass.getSimpleName();
            final var operationPath = resolveOperationPath(handlerMethod);
            final var pathSegments = splitPathSegments(operationPath);
            final var requestName = resolveRequestName(pathSegments, className, handlerMethod.getMethod().getName());
            final var tags = buildFolderTags(pathSegments);

            operation.setTags(tags);

            if (operation.getSummary() == null || operation.getSummary().isBlank()) {
                operation.setSummary(requestName);
            }

            if (operation.getOperationId() == null ||
                operation.getOperationId().isBlank() ||
                operation.getOperationId().startsWith("execute")) {

                operation.setOperationId(Objects.requireNonNullElseGet(
                    buildOperationId(pathSegments),
                    () -> className + "_" + handlerMethod.getMethod().getName()));
            }

            return operation;
        };
    }

    private static String resolveOperationPath(final org.springframework.web.method.HandlerMethod handlerMethod) {

        final var classMapping = AnnotatedElementUtils.findMergedAnnotation(
            handlerMethod.getBeanType(),
            RequestMapping.class);
        final var methodMapping = AnnotatedElementUtils.findMergedAnnotation(
            handlerMethod.getMethod(),
            RequestMapping.class);
        final var classPath = resolveFirstPath(classMapping);
        final var methodPath = resolveFirstPath(methodMapping);

        if (classPath == null) {
            return methodPath;
        }

        if (methodPath == null) {
            return classPath;
        }

        return joinPaths(classPath, methodPath);
    }

    private static String resolveFirstPath(final RequestMapping requestMapping) {

        if (requestMapping == null) {
            return null;
        }

        final var values = requestMapping.value();

        if (values.length > 0 && !values[0].isBlank()) {
            return values[0];
        }

        final var paths = requestMapping.path();

        if (paths.length > 0 && !paths[0].isBlank()) {
            return paths[0];
        }

        return null;
    }

    private static String joinPaths(final String classPath, final String methodPath) {

        final var normalizedClassPath = trimSlashes(classPath);
        final var normalizedMethodPath = trimSlashes(methodPath);

        if (normalizedClassPath.isEmpty()) {
            return "/" + normalizedMethodPath;
        }

        if (normalizedMethodPath.isEmpty()) {
            return "/" + normalizedClassPath;
        }

        return "/" + normalizedClassPath + "/" + normalizedMethodPath;
    }

    private static List<String> splitPathSegments(final String path) {

        if (path == null || path.isBlank()) {
            return List.of();
        }

        return Arrays.stream(path.split("/"))
            .filter(segment -> !segment.isBlank())
            .toList();
    }

    private static List<String> buildFolderTags(final List<String> pathSegments) {

        if (pathSegments.size() <= 1) {
            return new ArrayList<>();
        }

        return new ArrayList<>(pathSegments.subList(0, pathSegments.size() - 1));
    }

    private static String resolveRequestName(final List<String> pathSegments,
                                             final String className,
                                             final String methodName) {

        if (!pathSegments.isEmpty()) {
            return pathSegments.get(pathSegments.size() - 1);
        }

        return className.replace("Controller", "") + "_" + methodName;
    }

    private static String buildOperationId(final List<String> pathSegments) {

        if (pathSegments.isEmpty()) {
            return null;
        }

        return String.join("_", pathSegments);
    }

    private static String trimSlashes(final String value) {

        final var leadingTrimmed = value.startsWith("/") ? value.substring(1) : value;

        return leadingTrimmed.endsWith("/") ? leadingTrimmed.substring(0, leadingTrimmed.length() - 1) : leadingTrimmed;
    }

    public interface RequiredDependencies { }

    public interface RequiredSettings {

        OpenApiConfiguration.ApiSettings apiSettings();

    }

    public record ApiSettings(String title, String version) { }

}
