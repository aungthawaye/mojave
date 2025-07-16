package io.mojaloop.core.participant.store.cache;

import io.mojaloop.common.datatype.identifier.participant.FspId;
import io.mojaloop.common.datatype.identifier.participant.OracleId;
import io.mojaloop.common.datatype.type.fspiop.FspCode;
import io.mojaloop.common.fspiop.model.core.PartyIdType;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.data.OracleData;

public interface ParticipantStore {

    FspData getFspData(FspId fspId);

    FspData getFspData(FspCode fspCode);

    OracleData getOracleData(OracleId oracleId);

    OracleData getOracleData(PartyIdType partyIdType);

}
