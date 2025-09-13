package io.mojaloop.core.participant.contract.command.oracle;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.participant.contract.exception.oracle.OracleAlreadyExistsException;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public interface CreateOracleCommand {

    Output execute(Input input) throws OracleAlreadyExistsException;

    record Input(@JsonProperty(required = true) @NotNull PartyIdType type,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String name,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_HTTP_URL_LENGTH) String baseUrl) { }

    record Output(OracleId oracleId) { }
}
