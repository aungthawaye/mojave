package io.mojaloop.core.participant.contract.command.oracle;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.participant.contract.exception.oracle.OracleIdNotFoundException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public interface ChangeOracleNameCommand {

    Output execute(Input input) throws OracleIdNotFoundException;

    record Input(@JsonProperty(required = true) @NotNull OracleId oracleId,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String name) { }

    record Output() { }
}
