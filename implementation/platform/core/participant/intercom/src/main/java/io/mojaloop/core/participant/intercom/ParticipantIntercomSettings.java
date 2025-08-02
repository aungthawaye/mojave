package io.mojaloop.core.participant.intercom;

import io.mojaloop.component.vault.Vault;
import io.mojaloop.core.participant.domain.ParticipantDomainSettings;
import org.springframework.context.annotation.Bean;

public class ParticipantIntercomSettings extends ParticipantDomainSettings implements ParticipantIntercomConfiguration.RequiredSettings {

    private final Vault vault;

    public ParticipantIntercomSettings(Vault vault) {

        super(vault);

        this.vault = vault;
    }

    @Bean
    @Override
    public ParticipantIntercomConfiguration.TomcatSettings tomcatSettings() {

        return this.vault.get(VaultPaths.TOMCAT_SETTINGS, ParticipantIntercomConfiguration.TomcatSettings.class);
    }

    public static class VaultPaths {

        public static final String TOMCAT_SETTINGS = "micro/core/participant/intercom/tomcat/settings";

    }

}
