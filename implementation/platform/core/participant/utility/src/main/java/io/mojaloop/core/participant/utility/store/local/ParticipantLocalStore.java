package io.mojaloop.core.participant.utility.store.local;

import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.data.OracleData;
import io.mojaloop.core.participant.utility.client.ParticipantClient;
import io.mojaloop.core.participant.utility.store.ParticipantStore;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class ParticipantLocalStore implements ParticipantStore {

    private final ParticipantClient participantClient;

    public ParticipantLocalStore(ParticipantClient participantClient) {

        assert participantClient != null;

        this.participantClient = participantClient;
    }

    @PostConstruct
    public void bootstrap() {

    }

    @Override
    public FspData getFspData(FspId fspId) {

        return null;
    }

    @Override
    public FspData getFspData(FspCode fspCode) {

        return null;
    }

    @Override
    public OracleData getOracleData(OracleId oracleId) {

        return null;
    }

    @Override
    public OracleData getOracleData(PartyIdType partyIdType) {

        return null;
    }

}
