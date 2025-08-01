package io.mojaloop.core.participant.intercom;

import io.mojaloop.core.participant.domain.ParticipantDomainConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = "io.mojaloop.core.participant.intercom")
@Import(value = {ParticipantDomainConfiguration.class})
public class ParticipantIntercomConfiguration implements ParticipantDomainConfiguration.RequiredBeans {

    public interface RequiredSettings extends ParticipantDomainConfiguration.RequiredSettings { }

}
