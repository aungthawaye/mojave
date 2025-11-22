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

package io.mojaloop.component.misc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.mojaloop.component.misc.jackson.conversion.BigDecimalConversion;
import io.mojaloop.component.misc.jackson.conversion.InstantConversion;
import io.mojaloop.component.misc.spring.SpringContext;
import io.mojaloop.component.misc.spring.event.EventPublisher;
import io.mojaloop.component.misc.spring.event.publisher.SpringEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;

@EnableAsync
public class MiscConfiguration {

    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster asyncEventMulticaster(TaskExecutor aysncTaskExecutor) {

        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(aysncTaskExecutor);

        return eventMulticaster;
    }

    @Bean
    public TaskExecutor aysncTaskExecutor() {

        var executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(0);
        executor.setMaxPoolSize(Integer.MAX_VALUE);
        executor.setKeepAliveSeconds(60);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setQueueCapacity(0); // No queue like cachedThreadPool
        executor.setThreadNamePrefix("async-event-");
        executor.initialize();
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);

        return executor;
    }

    @Bean
    public EventPublisher eventPublisher(ApplicationEventPublisher applicationEventPublisher) {

        return new SpringEventPublisher(applicationEventPublisher);
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {

        var objectMapper = JsonMapper.builder().build();

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        objectMapper.findAndRegisterModules();

        var module = new SimpleModule().addSerializer(Instant.class, new InstantConversion.Serializer())
                                       .addDeserializer(Instant.class, new InstantConversion.Deserializer())
                                       .addSerializer(BigDecimal.class, new BigDecimalConversion.Serializer())
                                       .addDeserializer(BigDecimal.class, new BigDecimalConversion.Deserializer())
                                       .addSerializer(Long.class, ToStringSerializer.instance)
                                       .addSerializer(Long.TYPE, ToStringSerializer.instance)
                                       .addSerializer(Integer.class, ToStringSerializer.instance)
                                       .addSerializer(Integer.TYPE, ToStringSerializer.instance)
                                       .addSerializer(Boolean.class, ToStringSerializer.instance)
                                       .addSerializer(Boolean.TYPE, ToStringSerializer.instance)
                                       .addSerializer(BigInteger.class, ToStringSerializer.instance);

        objectMapper.registerModule(module);

        // Force millis, not seconds.nanos
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        objectMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);

        return objectMapper;
    }

    @Bean
    public SpringContext springContext() {

        return new SpringContext();
    }

    public interface RequiredBeans { }

    public interface RequiredSettings { }

}
