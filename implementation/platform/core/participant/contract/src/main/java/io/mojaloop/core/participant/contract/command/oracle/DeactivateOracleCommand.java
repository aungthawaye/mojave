package io.mojaloop.core.participant.contract.command.oracle;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.participant.contract.exception.oracle.OracleIdNotFoundException;
import jakarta.validation.constraints.NotNull;

public interface DeactivateOracleCommand {

    Output execute(Input input) throws OracleIdNotFoundException;

    record Input(@JsonProperty(required = true) @NotNull OracleId oracleId) { }

    record Output() { }
}
