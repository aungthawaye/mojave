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

package org.mojave.component.misc.handy;

import org.mojave.component.misc.error.MojaveErrorResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;

public final class ExceptionResolver {

    private static final Map<String, Map<String, ExceptionFactory>> CACHE =
        new ConcurrentHashMap<>();

    private ExceptionResolver() {

    }

    public static Throwable resolve(final String exceptionPackage,
                                    final MojaveErrorResponse error) {

        final var exceptionFactory = CACHE.computeIfAbsent(
            exceptionPackage, ExceptionResolver::initializeExceptionFactories).get(error.code());

        if (exceptionFactory == null) {
            return new RuntimeException(error.message());
        }

        return exceptionFactory.create(error.extras());
    }

    private static Set<String> findExceptionClassNames(final String exceptionPackage) {

        final var classNames = new HashSet<String>();
        final var exceptionPackagePath = exceptionPackage.replace('.', '/');

        try {

            final Enumeration<URL> resources = ExceptionResolver.class
                                                   .getClassLoader()
                                                   .getResources(exceptionPackagePath);

            while (resources.hasMoreElements()) {

                final var resource = resources.nextElement();
                final var protocol = resource.getProtocol();

                if ("file".equals(protocol)) {
                    classNames.addAll(findExceptionClassNamesInDirectory(
                        exceptionPackage, resource));
                    continue;
                }

                if ("jar".equals(protocol)) {
                    classNames.addAll(findExceptionClassNamesInJar(
                        exceptionPackagePath, resource));
                }
            }

        } catch (final IOException | URISyntaxException exception) {

            throw new IllegalStateException("Failed to initialize exception resolver.", exception);
        }

        return classNames;
    }

    private static Set<String> findExceptionClassNamesInDirectory(final String exceptionPackage,
                                                                  final URL resource)
        throws URISyntaxException, IOException {

        final var classNames = new HashSet<String>();
        final var rootPath = Path.of(resource.toURI());

        try (var paths = Files.walk(rootPath)) {

            paths
                .filter(Files::isRegularFile)
                .map(rootPath::relativize)
                .map(Path::toString)
                .filter(name -> name.endsWith(".class"))
                .filter(name -> !name.contains("$"))
                .map(name -> name.substring(0, name.length() - 6))
                .map(name -> name.replace('/', '.').replace('\\', '.'))
                .map(name -> exceptionPackage + "." + name)
                .forEach(classNames::add);
        }

        return classNames;
    }

    private static Set<String> findExceptionClassNamesInJar(final String exceptionPackagePath,
                                                            final URL resource)
        throws IOException {

        final var classNames = new HashSet<String>();
        final var connection = (JarURLConnection) resource.openConnection();

        try (var jarFile = connection.getJarFile()) {

            final var entries = jarFile.entries();

            while (entries.hasMoreElements()) {

                final JarEntry entry = entries.nextElement();
                final var name = entry.getName();

                if (!name.startsWith(exceptionPackagePath) || !name.endsWith(".class")) {
                    continue;
                }

                if (name.contains("$")) {
                    continue;
                }

                final var className = name.substring(0, name.length() - 6).replace('/', '.');

                classNames.add(className);
            }
        }

        return classNames;
    }

    private static Map<String, ExceptionFactory> initializeExceptionFactories(
        final String exceptionPackage) {

        final var exceptionFactories = new LinkedHashMap<String, ExceptionFactory>();

        for (final var className : findExceptionClassNames(exceptionPackage)) {
            registerExceptionFactory(className, exceptionFactories);
        }

        return Map.copyOf(exceptionFactories);
    }

    private static Throwable invokeFactory(final Method fromMethod,
                                           final Map<String, String> extras) {

        try {

            return (Throwable) fromMethod.invoke(null, extras == null ? Map.of() : extras);

        } catch (final IllegalAccessException | InvocationTargetException exception) {

            throw new IllegalStateException("Failed to create resolved exception.", exception);
        }
    }

    private static void registerExceptionFactory(
        final String className,
        final Map<String, ExceptionFactory> exceptionFactories) {

        try {

            final var clazz = Class.forName(className);

            if (!Throwable.class.isAssignableFrom(clazz)) {
                return;
            }

            final var codeField = clazz.getField("CODE");

            if (!Modifier.isStatic(codeField.getModifiers())) {
                return;
            }

            final var fromMethod = clazz.getMethod("from", Map.class);

            if (!Modifier.isStatic(fromMethod.getModifiers())) {
                return;
            }

            final var code = (String) codeField.get(null);

            if (exceptionFactories.containsKey(code)) {
                throw new IllegalStateException("Duplicate exception code: " + code);
            }

            exceptionFactories.put(code, extras -> invokeFactory(fromMethod, extras));

        } catch (final NoSuchFieldException | NoSuchMethodException exception) {

            // Ignore classes that do not follow the CODE + from(extras) convention.

        } catch (final ClassNotFoundException | IllegalAccessException exception) {

            throw new IllegalStateException(
                "Failed to register exception factory: " + className, exception);
        }
    }

    @FunctionalInterface
    private interface ExceptionFactory {

        Throwable create(Map<String, String> extras);

    }

}
