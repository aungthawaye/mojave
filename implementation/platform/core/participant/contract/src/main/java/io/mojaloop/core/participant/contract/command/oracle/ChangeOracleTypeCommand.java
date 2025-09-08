package io.mojaloop.core.participant.contract.command.oracle;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.participant.contract.exception.OracleAlreadyExistsException;
import io.mojaloop.core.participant.contract.exception.OracleIdNotFoundException;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import jakarta.validation.constraints.NotNull;

public interface ChangeOracleTypeCommand {

    Output execute(Input input) throws OracleAlreadyExistsException, OracleIdNotFoundException;

    record Input(@JsonProperty(required = true) @NotNull OracleId oracleId,
                 @JsonProperty(required = true) @NotNull PartyIdType type) { }

    record Output() { }
}
