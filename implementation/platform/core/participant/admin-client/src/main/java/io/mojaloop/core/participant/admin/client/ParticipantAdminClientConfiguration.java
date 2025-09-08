package io.mojaloop.core.participant.admin.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.participant.admin.client.service.ParticipantAdminService;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import(value = {MiscConfiguration.class})
@ComponentScan(basePackages = {"io.mojaloop.core.participant.admin.client"})
public class ParticipantAdminClientConfiguration {

    @Bean
    public ParticipantAdminService.FspCommands fspCommands(ParticipantAdminService.Settings settings, ObjectMapper objectMapper) {

        return RetrofitService.newBuilder(ParticipantAdminService.FspCommands.class, settings.baseUrl())
                              .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true).withDefaultFactories(objectMapper).build();
    }

    @Bean
    public ParticipantAdminService.OracleCommands oracleCommands(ParticipantAdminService.Settings settings, ObjectMapper objectMapper) {

        return RetrofitService.newBuilder(ParticipantAdminService.OracleCommands.class, settings.baseUrl())
                              .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true).withDefaultFactories(objectMapper).build();
    }

    @Bean
    public ParticipantAdminService.HubCommands hubCommands(ParticipantAdminService.Settings settings, ObjectMapper objectMapper) {

        return RetrofitService.newBuilder(ParticipantAdminService.HubCommands.class, settings.baseUrl())
                              .withHttpLogging(HttpLoggingInterceptor.Level.BODY, true).withDefaultFactories(objectMapper).build();
    }

    public interface RequiredBeans extends MiscConfiguration.RequiredBeans { }

    public interface RequiredSettings extends MiscConfiguration.RequiredSettings {

        ParticipantAdminService.Settings participantCommandServiceSettings();

    }

}
