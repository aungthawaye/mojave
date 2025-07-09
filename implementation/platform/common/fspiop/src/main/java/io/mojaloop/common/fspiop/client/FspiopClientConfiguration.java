package io.mojaloop.common.fspiop.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.common.component.ComponentConfiguration;
import io.mojaloop.common.component.retrofit.RetrofitService;
import io.mojaloop.common.component.retrofit.converter.NullOrEmptyConverterFactory;
import io.mojaloop.common.fspiop.service.AccountLookUpService;
import io.mojaloop.common.fspiop.service.QuotingService;
import io.mojaloop.common.fspiop.service.TransferService;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Import(value = {ComponentConfiguration.class})
public class FspiopClientConfiguration {

    @Bean
    public AccountLookUpService accountLookUpService(AccountLookUpService.Settings settings, ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(AccountLookUpService.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withConverterFactories(new NullOrEmptyConverterFactory(), ScalarsConverterFactory.create(),
                                           JacksonConverterFactory.create(objectMapper))
                   .build();
    }

    @Bean
    public QuotingService quotingService(QuotingService.Settings settings, ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(QuotingService.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withConverterFactories(new NullOrEmptyConverterFactory(), ScalarsConverterFactory.create(),
                                           JacksonConverterFactory.create(objectMapper))
                   .build();
    }

    @Bean
    public TransferService transferService(TransferService.Settings settings, ObjectMapper objectMapper) {

        return RetrofitService
                   .newBuilder(TransferService.class, settings.baseUrl())
                   .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true)
                   .withConverterFactories(new NullOrEmptyConverterFactory(), ScalarsConverterFactory.create(),
                                           JacksonConverterFactory.create(objectMapper))
                   .build();
    }

}
