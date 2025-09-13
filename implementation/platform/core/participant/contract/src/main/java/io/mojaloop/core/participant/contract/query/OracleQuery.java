package io.mojaloop.core.participant.contract.query;

import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.participant.contract.data.OracleData;
import io.mojaloop.core.participant.contract.exception.oracle.OracleIdNotFoundException;
import io.mojaloop.core.participant.contract.exception.oracle.OracleTypeNotFoundException;
import io.mojaloop.fspiop.spec.core.PartyIdType;

import java.util.List;
import java.util.Optional;

public interface OracleQuery {

    Optional<OracleData> find(PartyIdType type);

    OracleData get(PartyIdType type) throws OracleTypeNotFoundException;

    OracleData get(OracleId oracleId) throws OracleIdNotFoundException;

    List<OracleData> getAll();

}
