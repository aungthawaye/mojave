package org.mojave.participant.domain;

import org.springframework.context.annotation.Import;

@Import(
    value = {
        ParticipantDomainConfiguration.class,
        ParticipantDomainTestSettings.class})
public class ParticipantDomainTestConfiguration { }
