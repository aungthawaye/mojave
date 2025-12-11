package org.mojave.core.participant.contract.command.ssp;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.mojave.core.common.datatype.identifier.participant.SspId;

public interface ActivateSspCommand {

    Output execute(Input input);

    record Input(@JsonProperty(required = true) @NotNull SspId sspId) { }

    record Output() { }
}
