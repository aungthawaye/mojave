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
package io.mojaloop.common.component.spring.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SpringWebSecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   Settings settings,
                                                   AuthenticationErrorWriter authenticationErrorWriter,
                                                   Authenticator authenticator) throws Exception {

        //@@formatter:off
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement((sessionManagement) ->
                                   sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(configure -> configure
                                                    .requestMatchers(settings.publicEndpoints).permitAll()
                                                    .requestMatchers("/secured/**").authenticated()
                                                    .anyRequest().authenticated())
            .addFilterBefore(new AuthenticationProcessFilter(authenticator), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new AuthenticationFailureFilter(authenticationErrorWriter), AuthenticationProcessFilter.class);
        //@@formatter:on

        return http.build();
    }

    public record Settings(String[] publicEndpoints) { }

}
