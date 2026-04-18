package org.mojave.core.participant.intercom.replier;

import org.mojave.component.nats.NatsConfiguration;
import org.mojave.core.participant.domain.ParticipantDomainConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import(
    value = {
        ParticipantDomainConfiguration.class,
        NatsConfiguration.class})
@ComponentScan(basePackages = {"org.mojave.core.participant.intercom.replier"})
public class ParticipantIntercomReplierConfiguration {

    public interface RequiredDependencies
        extends ParticipantDomainConfiguration.RequiredDependencies { }

    public interface RequiredSettings extends ParticipantDomainConfiguration.RequiredSettings,
                                              NatsConfiguration.RequiredSettings { }

}
