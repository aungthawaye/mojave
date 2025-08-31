package io.mojaloop.core.participant.admin.client;

import io.mojaloop.core.participant.admin.client.service.ParticipantAdminService;
import org.springframework.context.annotation.Bean;

public class TestSettings implements ParticipantAdminClientConfiguration.RequiredSettings{

    @Bean
    @Override
    public ParticipantAdminService.Settings participantCommandServiceSettings() {

        return new ParticipantAdminService.Settings("http://localhost:4101");
    }

}
