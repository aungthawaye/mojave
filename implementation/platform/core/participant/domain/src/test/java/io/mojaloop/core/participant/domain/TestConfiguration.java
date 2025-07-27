package io.mojaloop.core.participant.domain;

import org.springframework.context.annotation.Import;

@Import({LocalVaultSettings.class, ParticipantDomainSettings.class, ParticipantDomainConfiguration.class})
public class TestConfiguration { }
