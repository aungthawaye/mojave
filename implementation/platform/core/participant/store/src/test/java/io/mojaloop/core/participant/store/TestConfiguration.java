package io.mojaloop.core.participant.store;

import io.mojaloop.core.participant.store.settings.ParticipantStoreVaultBasedSettings;
import org.springframework.context.annotation.Import;

@Import({LocalVaultSettings.class, ParticipantStoreConfiguration.class, ParticipantStoreVaultBasedSettings.class})
public class TestConfiguration { }
