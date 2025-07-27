package io.mojaloop.common.component.spring.security;

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
