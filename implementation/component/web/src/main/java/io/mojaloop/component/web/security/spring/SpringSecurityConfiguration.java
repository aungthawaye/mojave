/*-
 * ================================================================================
 * Mojaloop OSS
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

package io.mojaloop.component.web.security.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SpringSecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   SpringSecurityConfigurer.Settings settings,
                                                   Authenticator authenticator,
                                                   AuthenticationErrorWriter authenticationErrorWriter) throws Exception {

        return SpringSecurityConfigurer.configure(httpSecurity, settings, authenticator, authenticationErrorWriter);
    }

    public interface RequiredBeans {

        AuthenticationErrorWriter authenticationErrorWriter();

        Authenticator authenticator();

    }

    public interface RequiredSettings {

        SpringSecurityConfigurer.Settings springSecuritySettings();

    }

}
