package io.mojaloop.core.participant.domain;

import org.springframework.context.annotation.Import;

@Import(value = {
    ParticipantDomainMicroConfiguration.class, ParticipantDomainMicroConfiguration.VaultBasedSettings.class, LocalVaultSettings.class})
public class TestConfiguration { }
