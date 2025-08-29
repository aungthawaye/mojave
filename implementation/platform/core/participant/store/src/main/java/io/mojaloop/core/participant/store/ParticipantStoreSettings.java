package io.mojaloop.core.participant.store;

import io.mojaloop.core.participant.intercom.client.service.ParticipantIntercomService;

class ParticipantStoreSettings implements ParticipantStoreConfiguration.RequiredSettings {

    @Override
    public ParticipantIntercomService.Settings participantIntercomServiceSettings() {

        return null;
    }

    @Override
    public ParticipantStoreConfiguration.Settings participantStoreSettings() {

        return null;
    }

}
