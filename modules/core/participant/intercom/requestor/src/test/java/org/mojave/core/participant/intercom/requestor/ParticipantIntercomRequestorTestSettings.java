package org.mojave.core.participant.intercom.requestor;

import org.mojave.component.nats.NatsConfiguration;
import org.springframework.context.annotation.Bean;

public class ParticipantIntercomRequestorTestSettings
    implements ParticipantIntercomRequestorConfiguration.RequiredSettings {

    @Bean
    @Override
    public NatsConfiguration.NatsSettings natsSettings() {

        return new NatsConfiguration.NatsSettings(
            new String[]{"nats://localhost:4222"},
            "participant-intercom-requestor", null, null, null, 5000, 10, 1000, false);
    }

}
