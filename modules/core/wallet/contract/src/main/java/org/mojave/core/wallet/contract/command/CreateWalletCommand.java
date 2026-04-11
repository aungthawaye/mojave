package org.mojave.core.wallet.contract.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.wallet.WalletId;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.component.misc.constraint.StringSizeConstraints;

public interface CreateWalletCommand {

    String SUBJECT_NAME = "sub-wallet.create-wallet-command";

    String TOPIC_NAME = "tp-wallet.create-wallet-command";

    Output execute(Input input);

    record Input(@JsonProperty(required = true) @NotNull WalletOwnerId walletOwnerId,
                 @JsonProperty(required = true) @NotNull Currency currency,
                 @JsonProperty(required = true) @NotNull String tag,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String name) { }

    record Output(WalletId walletId) { }

}
