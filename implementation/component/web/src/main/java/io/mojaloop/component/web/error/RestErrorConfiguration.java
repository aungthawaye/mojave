package io.mojaloop.component.web.error;

import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackageClasses = RestErrorControllerAdvice.class)
public class RestErrorConfiguration {
}
