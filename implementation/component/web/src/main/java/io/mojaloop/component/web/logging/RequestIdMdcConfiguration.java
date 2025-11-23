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

//    @Bean
//    @Order(Ordered.HIGHEST_PRECEDENCE)
//    public RequestIdMdcOncePerRequestFilter requestIdMdcRequestFilter() {
//
//        return new RequestIdMdcOncePerRequestFilter();
//    }

}
