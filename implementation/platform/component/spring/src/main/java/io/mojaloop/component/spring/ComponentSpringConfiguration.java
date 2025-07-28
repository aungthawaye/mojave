package io.mojaloop.component.spring;

import io.mojaloop.component.spring.context.SpringContext;
import org.springframework.context.annotation.Bean;

public class ComponentSpringConfiguration {

    @Bean
    public SpringContext springContext() {

        return new SpringContext();
    }

    public interface RequiredSettings { }

}
