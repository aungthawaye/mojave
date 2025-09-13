package io.mojaloop.core.participant.contract.command.fsp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.participant.contract.exception.fsp.FspIdNotFoundException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface DeactivateEndpointCommand {

    Output execute(Input input) throws FspIdNotFoundException;

    record Input(@JsonProperty(required = true) @NotNull FspId fspId,
                 @JsonProperty(required = true) @NotNull @NotBlank EndpointType endpointType) { }

    record Output() { }

}
