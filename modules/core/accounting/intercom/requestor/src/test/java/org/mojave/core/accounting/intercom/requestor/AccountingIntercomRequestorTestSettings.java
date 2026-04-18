package org.mojave.core.accounting.intercom.requestor;

import org.mojave.component.nats.NatsConfiguration;
import org.springframework.context.annotation.Bean;

public class AccountingIntercomRequestorTestSettings
    implements AccountingIntercomRequestorConfiguration.RequiredSettings {

    @Bean
    @Override
    public NatsConfiguration.NatsSettings natsSettings() {

        return new NatsConfiguration.NatsSettings(
            new String[]{"nats://localhost:4222"},
            "participant-intercom-requestor", null, null, null, 5000, 10, 1000, false);
    }

}
