package io.mojaloop.core.participant.contract.command.fsp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.core.common.datatype.enumeration.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.participant.contract.exception.CannotActivateEndpointException;
import io.mojaloop.core.participant.contract.exception.FspIdNotFoundException;
import jakarta.validation.constraints.NotNull;

public interface ActivateEndpointCommand {

    Output execute(Input input) throws FspIdNotFoundException, CannotActivateEndpointException;

    record Input(@JsonProperty(required = true) @NotNull FspId fspId,
                 @JsonProperty(required = true) @NotNull EndpointType endpointType) { }

    record Output(boolean activated) { }

}
