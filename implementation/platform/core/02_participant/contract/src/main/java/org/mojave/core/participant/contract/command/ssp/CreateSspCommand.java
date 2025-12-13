package org.mojave.core.participant.contract.command.ssp;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.core.common.datatype.identifier.participant.SspId;
import org.mojave.core.common.datatype.type.participant.SspCode;
import org.mojave.fspiop.spec.core.Currency;

public interface CreateSspCommand {

    Output execute(Input input);

    record Input(@JsonProperty(required = true) @NotNull SspCode sspCode,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String name,
                 @JsonProperty(required = true) @NotNull Currency[] currencies,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_HTTP_URL_LENGTH) String baseUrl) { }

    record Output(SspId sspId) { }
}
