package org.mojave.core.participant.intercom.requestor;

import org.mojave.component.misc.MiscConfiguration;
import org.mojave.component.nats.NatsConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import(value = {MiscConfiguration.class, NatsConfiguration.class})
@ComponentScan(basePackages = {"org.mojave.core.participant.intercom.requestor"})
public class ParticipantIntercomRequestorConfiguration
    implements MiscConfiguration.RequiredDependencies {

    public interface RequiredDependencies extends MiscConfiguration.RequiredDependencies { }

    public interface RequiredSettings
        extends MiscConfiguration.RequiredSettings, NatsConfiguration.RequiredSettings { }

}
