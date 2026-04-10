package org.mojave.participant.intercom.replier;

import org.mojave.component.nats.NatsConfiguration;
import org.mojave.participant.domain.ParticipantDomainConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import(
    value = {
        ParticipantDomainConfiguration.class,
        NatsConfiguration.class})
@ComponentScan(basePackages = {"org.mojave.participant.intercom.replier"})
public class ParticipantIntercomReplierConfiguration {

    public interface RequiredDependencies
        extends ParticipantDomainConfiguration.RequiredDependencies { }

    public interface RequiredSettings extends ParticipantDomainConfiguration.RequiredSettings,
                                              NatsConfiguration.RequiredSettings { }

}
