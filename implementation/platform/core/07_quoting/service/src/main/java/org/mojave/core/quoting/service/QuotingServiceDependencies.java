package org.mojave.core.quoting.service;

import org.mojave.core.common.datatype.type.participant.FspCode;
import org.mojave.core.participant.store.ParticipantStore;
import org.mojave.fspiop.service.component.ParticipantVerifier;
import org.springframework.context.annotation.Bean;

public class QuotingServiceDependencies
    implements QuotingServiceConfiguration.RequiredDependencies {

    private final ParticipantStore participantStore;

    public QuotingServiceDependencies(ParticipantStore participantStore) {

        assert participantStore != null;

        this.participantStore = participantStore;
    }

    @Bean
    @Override
    public ParticipantVerifier participantVerifier() {

        return fspCode -> this.participantStore.getFspData(new FspCode(fspCode)) != null;
    }

}
