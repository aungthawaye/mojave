package org.mojave.accounting.intercom.producer;

import org.mojave.component.nats.NatsConfiguration;
import org.springframework.context.annotation.Bean;

public class AccountingIntercomProducerSettings
    implements AccountingIntercomProducerConfiguration.RequiredSettings {

    @Bean
    @Override
    public NatsConfiguration.NatsSettings natsSettings() {

        return new NatsConfiguration.NatsSettings(
            new String[]{"nats://localhost:4222"},
            "accounting-intercom-producer", null, null, null, 5000, 10, 1000, false);
    }

}
