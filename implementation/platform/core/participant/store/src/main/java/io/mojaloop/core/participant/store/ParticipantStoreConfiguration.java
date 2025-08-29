package io.mojaloop.core.participant.store;

import io.mojaloop.core.participant.intercom.client.ParticipantIntercomClientConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import(value = {ParticipantIntercomClientConfiguration.class})
@ComponentScan(basePackages = {"io.mojaloop.core.participant.store"})
public class ParticipantStoreConfiguration implements ParticipantIntercomClientConfiguration.RequiredBeans {

    public interface RequiredBeans extends ParticipantIntercomClientConfiguration.RequiredBeans { }

    public interface RequiredSettings extends ParticipantIntercomClientConfiguration.RequiredSettings {

        ParticipantStoreConfiguration.Settings participantStoreSettings();

    }

    public record Settings(int refreshIntervalMs) { }

}
