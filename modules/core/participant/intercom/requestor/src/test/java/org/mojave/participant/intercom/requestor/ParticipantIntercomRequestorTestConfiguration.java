package org.mojave.participant.intercom.requestor;

import org.springframework.context.annotation.Import;

@Import(
    value = {
        ParticipantIntercomRequestorConfiguration.class,
        ParticipantIntercomRequestorSettings.class})
public class ParticipantIntercomRequestorTestConfiguration { }

