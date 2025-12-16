/*-
 * ================================================================================
 * Mojave
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
package org.mojave.component.misc;

import com.amazon.corretto.crypto.provider.AmazonCorrettoCryptoProvider;
import org.mojave.component.misc.jackson.ObjectMapperFactory;
import org.mojave.component.misc.logger.ObjectLoggerInitializer;
import org.mojave.component.misc.spring.SpringContext;
import org.mojave.component.misc.spring.event.EventPublisher;
import org.mojave.component.misc.spring.event.publisher.SpringEventPublisher;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.Executor;

@EnableAsync
public class MiscConfiguration {

    static {
        AmazonCorrettoCryptoProvider.install();
        AmazonCorrettoCryptoProvider.INSTANCE.assertHealthy();
    }

    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster applicationEventMulticaster(Executor eventVirtualThreadExecutor) {

        var multicaster = new SimpleApplicationEventMulticaster();

        multicaster.setTaskExecutor(eventVirtualThreadExecutor);

        return multicaster;
    }

    @Bean
    public EventPublisher eventPublisher(ApplicationEventPublisher applicationEventPublisher) {

        return new SpringEventPublisher(applicationEventPublisher);
    }

    @Bean(name = "eventVirtualThreadExecutor")
    public Executor eventVirtualThreadExecutor(TaskDecorator mdcTaskDecorator) {

        var executor = new SimpleAsyncTaskExecutor("event-vt-");

        //executor.setTaskDecorator(mdcTaskDecorator);
        executor.setVirtualThreads(true); // 🔹 switch to virtual threads

        return executor;
    }

    /**
     * Copies MDC from the caller thread into the virtual thread.
     */
    @Bean
    public TaskDecorator mdcTaskDecorator() {

        return runnable -> {

            Map<String, String> contextMap = MDC.getCopyOfContextMap();

            return () -> {

                Map<String, String> previous = MDC.getCopyOfContextMap();

                if (contextMap != null) {

                    MDC.setContextMap(contextMap);

                } else {

                    MDC.clear();
                }

                try {

                    runnable.run();

                } finally {

                    if (previous != null) {

                        MDC.setContextMap(previous);

                    } else {

                        MDC.clear();
                    }
                }
            };
        };
    }

    @Bean
    public ObjectLoggerInitializer objectLoggerInitializer(ObjectMapper objectMapper) {

        return new ObjectLoggerInitializer(objectMapper);
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {

        return ObjectMapperFactory.createJsonMapper();
    }

    @Bean
    public SpringContext springContext() {

        return new SpringContext();
    }

    public interface RequiredBeans { }

    public interface RequiredSettings { }

}
