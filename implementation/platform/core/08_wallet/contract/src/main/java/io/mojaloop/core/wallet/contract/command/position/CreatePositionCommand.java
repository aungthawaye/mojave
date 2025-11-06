package io.mojaloop.core.wallet.contract.command.position;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public interface CreatePositionCommand {

    Output execute(Input input);

    record Input(@JsonProperty(required = true) @NotNull WalletOwnerId walletOwnerId,
                 @JsonProperty(required = true) @NotNull Currency currency,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String name,
                 @JsonProperty(required = true) @NotNull BigDecimal netDebitCap) { }

    record Output(PositionId positionId) { }

}
