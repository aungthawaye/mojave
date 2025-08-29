package io.mojaloop.core.participant.intercom.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.participant.intercom.client.service.ParticipantIntercomService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import(value = {MiscConfiguration.class})
@ComponentScan(basePackages = {"io.mojaloop.core.participant.intercom.client"})
public class ParticipantIntercomClientConfiguration implements MiscConfiguration.RequiredBeans {

    @Bean
    public ParticipantIntercomService participantIntercomService(ParticipantIntercomService.Settings settings, ObjectMapper objectMapper) {

        return RetrofitService.newBuilder(ParticipantIntercomService.class, settings.baseUrl()).withDefaultFactories(objectMapper).build();
    }

    public interface RequiredBeans extends MiscConfiguration.RequiredBeans {

    }

    public interface RequiredSettings extends MiscConfiguration.RequiredSettings {

        ParticipantIntercomService.Settings participantIntercomServiceSettings();

    }

}
