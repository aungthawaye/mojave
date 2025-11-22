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
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class P12Reader {

    private static final Logger LOGGER = LoggerFactory.getLogger(P12Reader.class);

    public static InputStream read(ContentType contentType, String contentValue)
        throws IOException {

        return switch (contentType) {
            case P12_FILE_CLASS_PATH -> {
                LOGGER.debug("Reading P12 file from classpath : {}", contentValue);
                yield new ClassPathResource(contentValue).getInputStream();
            }
            case P12_FILE_ABSOLUTE_PATH -> {
                LOGGER.debug("Reading P12 file from absolute path : {}", contentValue);
                yield Files.newInputStream(Paths.get(contentValue));
            }
            case P12_BASE64_CLASS_PATH -> {
                LOGGER.debug("Reading P12(Base64) file from classpath : {}", contentValue);
                String filePath = new ClassPathResource(contentValue).getFile().getAbsolutePath();
                String base64Content = Files.readString(Paths.get(filePath));
                yield new ByteArrayInputStream(
                    Base64.getMimeDecoder().decode(base64Content.getBytes()));
            }
            case P12_BASE64_ABSOLUTE_PATH -> {
                LOGGER.debug("Reading P12(Base64) file from absolute path : {}", contentValue);
                String base64Content = Files.readString(Paths.get(contentValue));
                yield new ByteArrayInputStream(
                    Base64.getMimeDecoder().decode(base64Content.getBytes()));
            }
            case P12_CONTENT_IN_BASE64 -> {
                LOGGER.debug("Reading P12 content in Base64 string");
                yield new ByteArrayInputStream(
                    Base64.getMimeDecoder().decode(contentValue.getBytes()));
            }
        };
    }

    public enum ContentType {
        P12_FILE_CLASS_PATH,
        P12_FILE_ABSOLUTE_PATH,
        P12_BASE64_CLASS_PATH,
        P12_BASE64_ABSOLUTE_PATH,
        P12_CONTENT_IN_BASE64
    }

}
