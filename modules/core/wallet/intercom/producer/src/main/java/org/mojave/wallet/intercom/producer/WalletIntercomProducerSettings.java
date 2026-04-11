package org.mojave.wallet.intercom.producer;

import org.mojave.component.nats.NatsConfiguration;
import org.springframework.context.annotation.Bean;

public class WalletIntercomProducerSettings
    implements WalletIntercomProducerConfiguration.RequiredSettings {

    @Bean
    @Override
    public NatsConfiguration.NatsSettings natsSettings() {

        return new NatsConfiguration.NatsSettings(
            new String[]{"nats://localhost:4222"},
            "wallet-intercom-producer", null, null, null, 5000, 10, 1000, false);
    }

}
