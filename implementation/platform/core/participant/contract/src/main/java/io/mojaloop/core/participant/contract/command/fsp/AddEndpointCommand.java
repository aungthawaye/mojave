package io.mojaloop.core.participant.contract.command.fsp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.identifier.participant.FspEndpointId;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.participant.contract.exception.fsp.FspEndpointAlreadyConfiguredException;
import io.mojaloop.core.participant.contract.exception.fsp.FspIdNotFoundException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public interface AddEndpointCommand {

    Output execute(Input input) throws FspEndpointAlreadyConfiguredException, FspIdNotFoundException;

    record Input(@JsonProperty(required = true) @NotNull FspId fspId,
                 @JsonProperty(required = true) @NotNull EndpointType endpointType,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_HTTP_URL_LENGTH) String baseUrl) { }

    record Output(FspEndpointId fspEndpointId) { }

}
