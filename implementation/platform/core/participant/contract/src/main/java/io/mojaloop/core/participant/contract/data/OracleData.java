package io.mojaloop.core.participant.contract.data;

import io.mojaloop.common.datatype.enumeration.ActivationStatus;
import io.mojaloop.common.datatype.enumeration.TerminationStatus;
import io.mojaloop.common.datatype.identifier.participant.OracleId;
import io.mojaloop.common.fspiop.model.core.PartyIdType;

import java.util.Objects;

public record OracleData(OracleId oracleId,
                         PartyIdType type,
                         String name,
                         String host,
                         ActivationStatus activationStatus,
                         TerminationStatus terminationStatus) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof OracleData that)) {
            return false;
        }
        return Objects.equals(oracleId, that.oracleId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(oracleId);
    }

}
