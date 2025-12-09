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

package io.mojaloop.component.web.spring.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

public class WebMvcExtension implements WebMvcConfigurer {

    private final ObjectMapper objectMapper;

    public WebMvcExtension(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

        for (int i = 0; i < converters.size(); i++) {

            HttpMessageConverter<?> c = converters.get(i);

            if (c instanceof MappingJackson2HttpMessageConverter) {

                converters.set(i, new MappingJackson2HttpMessageConverter(this.objectMapper));
                return;
            }
        }

        converters.addFirst(new MappingJackson2HttpMessageConverter(this.objectMapper));
    }

}
