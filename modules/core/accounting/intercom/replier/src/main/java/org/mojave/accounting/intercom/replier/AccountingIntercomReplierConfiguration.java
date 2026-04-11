package org.mojave.accounting.intercom.replier;

import org.mojave.accounting.domain.AccountingDomainConfiguration;
import org.mojave.component.nats.NatsConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import(
    value = {
        AccountingDomainConfiguration.class,
        NatsConfiguration.class,})
@ComponentScan(basePackages = {"org.mojave.accounting.intercom.replier"})
public class AccountingIntercomReplierConfiguration {

    public interface RequiredDependencies
        extends AccountingDomainConfiguration.RequiredDependencies { }

    public interface RequiredSettings extends AccountingDomainConfiguration.RequiredSettings,
                                              NatsConfiguration.RequiredSettings { }

}
