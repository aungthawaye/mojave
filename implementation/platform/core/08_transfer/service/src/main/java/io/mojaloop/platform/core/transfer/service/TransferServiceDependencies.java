package io.mojaloop.platform.core.transfer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.contract.query.FspQuery;
import io.mojaloop.core.participant.contract.query.OracleQuery;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.core.participant.store.strategy.timer.ParticipantTimerStore;
import io.mojaloop.core.transfer.contract.component.interledger.AgreementUnwrapper;
import io.mojaloop.core.transfer.domain.component.interledger.unwrapper.MojaveAgreementUnwrapper;
import io.mojaloop.fspiop.service.component.ParticipantVerifier;
import org.springframework.context.annotation.Bean;

public class TransferServiceDependencies
    implements TransferServiceConfiguration.RequiredDependencies {

    private final ParticipantStore participantStore;

    private final ObjectMapper objectMapper;

    public TransferServiceDependencies(FspQuery fspQuery,
                                       OracleQuery oracleQuery,
                                       ObjectMapper objectMapper) {

        assert fspQuery != null;
        assert oracleQuery != null;
        assert objectMapper != null;

        this.participantStore = new ParticipantTimerStore(
            fspQuery, oracleQuery, new ParticipantTimerStore.Settings(
            Integer.parseInt(System.getenv("PARTICIPANT_STORE_REFRESH_INTERVAL_MS"))));

        this.objectMapper = objectMapper;
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

    @Bean
    @Override
    public AgreementUnwrapper partyUnwrapper() {

        return new MojaveAgreementUnwrapper(this.objectMapper);
    }

}
