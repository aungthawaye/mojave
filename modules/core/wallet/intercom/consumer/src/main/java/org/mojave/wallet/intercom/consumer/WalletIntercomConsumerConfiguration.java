package org.mojave.wallet.intercom.consumer;

import org.mojave.component.nats.NatsConfiguration;
import org.mojave.wallet.domain.WalletDomainConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import(
    value = {
        WalletDomainConfiguration.class,
        NatsConfiguration.class})
@ComponentScan(basePackages = {"org.mojave.wallet.intercom.consumer"})
public class WalletIntercomConsumerConfiguration {

    public interface RequiredDependencies
        extends WalletDomainConfiguration.RequiredDependencies { }

    public interface RequiredSettings extends WalletDomainConfiguration.RequiredSettings,
                                              NatsConfiguration.RequiredSettings { }

}
