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
public class ReadAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadAspect.class);

    @Around(
        "execution(public * *(..)) && @annotation(io.mojaloop.common.component.jpa.annotation.Read)")
    public Object aroundTransactional(ProceedingJoinPoint joinPoint) throws Throwable {

        try {

            RoutingDataSource.setDataSourceKey("read");
            LOGGER.debug("ReadAspect : RoutingDataSource -> read");
            return joinPoint.proceed();

        } finally {

            RoutingDataSource.clearDataSourceKey();
        }
    }

}
