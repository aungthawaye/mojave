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

package io.mojaloop.component.misc.handy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ContentLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentLoader.class);

    public static InputStream from(String location) throws IOException {

        return ContentLoader.open(location);
    }

    private static InputStream open(String location) throws IOException {

        Path path = Paths.get(location);

        if (Files.exists(path)) {
            return Files.newInputStream(path);
        }

        // Try classpath (with optional "classpath:" prefix)
        boolean isClasspath = location.startsWith("classpath:");
        String cpLocation = isClasspath ? location.substring("classpath:".length()) : location;
        cpLocation = cpLocation.startsWith("/") ? cpLocation.substring(1) : cpLocation;

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream is = cl.getResourceAsStream(cpLocation);

        if (is != null) {
            return is;
        }

        throw new IOException("File not found on filesystem or classpath: " + location);
    }

}
