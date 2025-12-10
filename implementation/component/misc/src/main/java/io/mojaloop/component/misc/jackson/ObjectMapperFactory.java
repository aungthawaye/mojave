package io.mojaloop.component.misc.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.mojaloop.component.misc.jackson.conversion.BigDecimalConversion;
import io.mojaloop.component.misc.jackson.conversion.InstantConversion;
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
