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
