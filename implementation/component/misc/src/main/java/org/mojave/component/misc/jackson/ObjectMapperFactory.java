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
package org.mojave.component.misc.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.mojave.component.misc.jackson.conversion.BigDecimalConversion;
import org.mojave.component.misc.jackson.conversion.InstantConversion;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;

public class ObjectMapperFactory {

    public static JsonMapper createJsonMapper() {

        var module = new SimpleModule()
                         .addSerializer(Instant.class, new InstantConversion.Serializer())
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

        var factory = JsonFactory
                          .builder()
                          .disable(StreamWriteFeature.AUTO_CLOSE_TARGET)
                          .build()
                          .rebuild()
                          .enable(StreamWriteFeature.STRICT_DUPLICATE_DETECTION)
                          .build();

        return JsonMapper
                   .builder(factory)
                   .changeDefaultPropertyInclusion(
                       incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                   .changeDefaultPropertyInclusion(
                       incl -> incl.withValueInclusion(JsonInclude.Include.NON_EMPTY))
                   .configure(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL, true)
                   .configure(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                   .configure(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                   .configure(DeserializationFeature.FAIL_ON_TRAILING_TOKENS, false)
                   .addModule(module)
                   .findAndAddModules()
                   .build();
    }

}
