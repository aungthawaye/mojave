package io.mojaloop.common.component.jpa.annotation.aspect;

import io.mojaloop.common.component.jpa.datasource.RoutingDataSource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class WriteAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(WriteAspect.class);

    @Around(
        "execution(public * *(..)) && @annotation(io.mojaloop.common.component.jpa.annotation.Write)")
    public Object aroundTransactional(ProceedingJoinPoint joinPoint) throws Throwable {

        try {

            RoutingDataSource.setDataSourceKey("write");
            LOGGER.debug("WriteAspect : RoutingDataSource -> write");
            return joinPoint.proceed();

        } finally {

            RoutingDataSource.clearDataSourceKey();
        }

    }

}
