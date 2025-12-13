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
package org.mojave.component.web.spring.mvc;

import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

public class WebMvcExtension extends WebMvcConfigurationSupport {

    private final ObjectMapper objectMapper;

    public WebMvcExtension(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;
    }

    @Override
    public void configureMessageConverters(HttpMessageConverters.ServerBuilder builder) {

        builder.registerDefaults();
        builder.withJsonConverter(
            new JacksonJsonHttpMessageConverter((JsonMapper) this.objectMapper));

    }

}
