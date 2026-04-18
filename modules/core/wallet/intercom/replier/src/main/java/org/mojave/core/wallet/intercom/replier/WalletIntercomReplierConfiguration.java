package org.mojave.core.wallet.intercom.replier;

import org.mojave.component.nats.NatsConfiguration;
import org.mojave.core.wallet.domain.WalletDomainConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import(
    value = {
        WalletDomainConfiguration.class,
        NatsConfiguration.class})
@ComponentScan(basePackages = {"org.mojave.core.wallet.intercom.replier"})
public class WalletIntercomReplierConfiguration {

    public interface RequiredDependencies
        extends WalletDomainConfiguration.RequiredDependencies { }

    public interface RequiredSettings extends WalletDomainConfiguration.RequiredSettings,
                                              NatsConfiguration.RequiredSettings { }

}
