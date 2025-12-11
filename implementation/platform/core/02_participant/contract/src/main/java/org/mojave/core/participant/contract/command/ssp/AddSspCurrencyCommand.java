package org.mojave.core.participant.contract.command.ssp;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.mojave.core.common.datatype.identifier.participant.SspCurrencyId;
import org.mojave.core.common.datatype.identifier.participant.SspId;
import org.mojave.fspiop.spec.core.Currency;

public interface AddSspCurrencyCommand {

    Output execute(Input input);

    record Input(@JsonProperty(required = true) @NotNull SspId sspId,
                 @JsonProperty(required = true) @NotNull Currency currency) { }

    record Output(SspCurrencyId sspCurrencyId) { }
}
