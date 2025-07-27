package io.mojaloop.common.component.spring.web;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

public class CorsConfigurer {

    public static CorsFilter configure(Settings settings) {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(settings.allowedOrigins()));
        configuration.setAllowedMethods(Arrays.asList(settings.allowedMethods()));
        configuration.setAllowedHeaders(Arrays.asList(settings.allowedHeaders()));
        //configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return new CorsFilter(source);

    }

    public record Settings(String[] allowedOrigins, String[] allowedMethods, String[] allowedHeaders) { }

}
