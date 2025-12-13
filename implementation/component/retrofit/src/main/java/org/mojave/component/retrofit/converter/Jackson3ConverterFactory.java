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
package org.mojave.component.retrofit.converter;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.jspecify.annotations.NonNull;
import retrofit2.Converter;
import retrofit2.Retrofit;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public final class Jackson3ConverterFactory extends Converter.Factory {

    private static final MediaType MEDIA_TYPE = MediaType.get("application/json; charset=UTF-8");

    private final ObjectMapper objectMapper;

    private Jackson3ConverterFactory(ObjectMapper objectMapper) {

        assert objectMapper != null;

        this.objectMapper = objectMapper;
    }

    public static Jackson3ConverterFactory create(ObjectMapper mapper) {

        return new Jackson3ConverterFactory(mapper);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(@NonNull Type type,
                                                          Annotation @NonNull [] parameterAnnotations,
                                                          Annotation @NonNull [] methodAnnotations,
                                                          @NonNull Retrofit retrofit) {

        JavaType javaType = objectMapper.getTypeFactory().constructType(type);

        return new JacksonRequestBodyConverter<>(objectMapper, javaType);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(@NonNull Type type,
                                                            Annotation @NonNull [] annotations,
                                                            @NonNull Retrofit retrofit) {

        JavaType javaType = objectMapper.getTypeFactory().constructType(type);

        return new JacksonResponseBodyConverter<>(objectMapper, javaType);
    }

    // ------- Inner converters -------

    static final class JacksonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

        private final ObjectMapper mapper;

        private final JavaType javaType;

        JacksonResponseBodyConverter(ObjectMapper mapper, JavaType javaType) {

            this.mapper = mapper;
            this.javaType = javaType;
        }

        @Override
        public T convert(ResponseBody value) throws IOException {

            try (value) {
                return mapper.readValue(value.byteStream(), javaType);
            }
        }

    }

    static final class JacksonRequestBodyConverter<T> implements Converter<T, RequestBody> {

        private final ObjectMapper mapper;

        private final JavaType javaType;

        JacksonRequestBodyConverter(ObjectMapper mapper, JavaType javaType) {

            this.mapper = mapper;
            this.javaType = javaType;
        }

        @Override
        public RequestBody convert(@NonNull T value) throws IOException {

            try (Buffer buffer = new Buffer()) {
                mapper.writerFor(javaType).writeValue(buffer.outputStream(), value);
                return RequestBody.create(buffer.readByteString(), MEDIA_TYPE);
            }
        }

    }

}
