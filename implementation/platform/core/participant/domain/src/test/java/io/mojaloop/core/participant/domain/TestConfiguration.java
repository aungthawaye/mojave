package io.mojaloop.core.participant.domain;

import io.mojaloop.core.participant.domain.settings.ParticipantVaultBasedSettings;
import org.springframework.context.annotation.Import;

@Import({LocalVaultSettings.class, ParticipantVaultBasedSettings.class, ParticipantDomainConfiguration.class})
public class TestConfiguration { }
