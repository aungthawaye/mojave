package io.mojaloop.core.wallet.contract.command.wallet;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public interface CreateWalletCommand {

    Output execute(Input input);

    record Input(@JsonProperty(required = true) @NotNull WalletOwnerId walletOwnerId,
                 @JsonProperty(required = true) @NotNull @NotBlank Currency currency,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String name) { }

    record Output(WalletId walletId) { }

}
