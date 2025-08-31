package io.mojaloop.core.participant.admin.client;

import org.springframework.context.annotation.Import;

@Import(value = {ParticipantAdminClientConfiguration.class, TestSettings.class})
public class TestConfiguration {
}
