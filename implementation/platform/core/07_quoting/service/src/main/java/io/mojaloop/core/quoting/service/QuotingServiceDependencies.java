package io.mojaloop.core.quoting.service;

import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.fspiop.service.component.ParticipantVerifier;
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
