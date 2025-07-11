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
package io.mojaloop.common.component.persistence.annotation.aspect;

import io.mojaloop.common.component.persistence.datasource.RoutingDataSource;
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
        "execution(public * *(..)) && @annotation(io.mojaloop.common.component.persistence.annotation.Write)")
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
