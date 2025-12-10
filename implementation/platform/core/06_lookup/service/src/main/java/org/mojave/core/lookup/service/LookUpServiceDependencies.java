package org.mojave.core.lookup.service;

import org.mojave.core.common.datatype.type.participant.FspCode;
import org.mojave.core.participant.contract.query.FspQuery;
import org.mojave.core.participant.contract.query.OracleQuery;
import org.mojave.core.participant.store.ParticipantStore;
import org.mojave.core.participant.store.strategy.timer.ParticipantTimerStore;
import org.mojave.fspiop.service.component.ParticipantVerifier;
import org.springframework.context.annotation.Bean;

public class LookUpServiceDependencies implements LookUpServiceConfiguration.RequiredDependencies {

    private final ParticipantStore participantStore;

    public LookUpServiceDependencies(FspQuery fspQuery, OracleQuery oracleQuery) {

        assert fspQuery != null;
        assert oracleQuery != null;

        this.participantStore = new ParticipantTimerStore(
            fspQuery, oracleQuery, new ParticipantTimerStore.Settings(
            Integer.parseInt(System.getenv("PARTICIPANT_STORE_REFRESH_INTERVAL_MS"))));
    }

    @Bean
    @Override
    public ParticipantStore participantStore() {

        return this.participantStore;
    }

    @Bean
    @Override
    public ParticipantVerifier participantVerifier() {

        return fspCode -> this.participantStore.getFspData(new FspCode(fspCode)) != null;
    }

}
