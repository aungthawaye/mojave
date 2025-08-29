package io.mojaloop.component.web.error;

import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackageClasses = ApplicationErrorControllerAdvice.class)
public class ApplicationErrorConfiguration {
}
