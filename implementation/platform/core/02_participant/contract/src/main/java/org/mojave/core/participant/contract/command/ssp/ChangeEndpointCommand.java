package org.mojave.core.participant.contract.command.ssp;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.core.common.datatype.identifier.participant.SspId;

public interface ChangeEndpointCommand {

    Output execute(Input input);

    record Input(@JsonProperty(required = true) @NotNull SspId sspId,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_HTTP_URL_LENGTH) String baseUrl) { }

    record Output(boolean changed) { }
}
