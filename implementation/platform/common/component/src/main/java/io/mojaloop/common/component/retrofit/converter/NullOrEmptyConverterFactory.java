package io.mojaloop.common.component.retrofit.converter;

import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class NullOrEmptyConverterFactory extends Converter.Factory {

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(@NotNull Type type, @NotNull Annotation[] annotations, Retrofit retrofit) {

        final Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
        return (Converter<ResponseBody, Object>) body -> body.contentLength() == 0 ? null : delegate.convert(body);

    }

}
