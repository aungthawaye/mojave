package io.mojaloop.core.lookup.service;

import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.contract.query.FspQuery;
import io.mojaloop.core.participant.contract.query.OracleQuery;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.core.participant.store.strategy.timer.ParticipantTimerStore;
import io.mojaloop.fspiop.service.component.ParticipantVerifier;
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
