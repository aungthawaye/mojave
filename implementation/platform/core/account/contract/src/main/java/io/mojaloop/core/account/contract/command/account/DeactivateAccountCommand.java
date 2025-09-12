package io.mojaloop.core.account.contract.command.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import jakarta.validation.constraints.NotNull;

public interface DeactivateAccountCommand {

    Output execute(Input input);

    /**
     * Input for deactivating an Account.
     */
    record Input(
        @JsonProperty(required = true) @NotNull AccountId accountId
    ) { }

    record Output(AccountId accountId) { }

}
