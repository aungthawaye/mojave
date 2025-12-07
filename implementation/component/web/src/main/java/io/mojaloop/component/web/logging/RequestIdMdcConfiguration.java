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

package io.mojaloop.component.web.logging;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

public class RequestIdMdcConfiguration {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public FilterRegistrationBean<RequestIdMdcFilter> requestIdMdcFilterRegistration() {

        FilterRegistrationBean<RequestIdMdcFilter> registration = new FilterRegistrationBean<>();

        registration.setFilter(new RequestIdMdcFilter());
        registration.addUrlPatterns("/*");
        registration.setName("requestIdMdcFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return registration;
    }

    public interface RequiredBeans { }

    public interface RequiredSettings { }

}
