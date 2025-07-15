package io.mojaloop.core.participant.contract.data;

import io.mojaloop.common.datatype.identifier.participant.OracleId;
import io.mojaloop.common.fspiop.model.core.PartyIdType;

public record OracleData(OracleId oracleId, PartyIdType type, String name, String host) { }
