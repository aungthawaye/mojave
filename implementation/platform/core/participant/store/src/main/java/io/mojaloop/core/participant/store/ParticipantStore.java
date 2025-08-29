package io.mojaloop.core.participant.store;

import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.data.OracleData;
import io.mojaloop.fspiop.spec.core.PartyIdType;

public interface ParticipantStore {

    FspData getFspData(FspId fspId);

    FspData getFspData(FspCode fspCode);

    OracleData getOracleData(OracleId oracleId);

    OracleData getOracleData(PartyIdType partyIdType);

}
