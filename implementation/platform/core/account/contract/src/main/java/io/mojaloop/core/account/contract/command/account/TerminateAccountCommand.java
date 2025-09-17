package io.mojaloop.core.account.contract.command.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.core.account.contract.exception.account.AccountIdNotFoundException;
import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import jakarta.validation.constraints.NotNull;

public interface TerminateAccountCommand {

    Output execute(Input input) throws AccountIdNotFoundException;

    record Input(@JsonProperty(required = true) @NotNull AccountId accountId) { }

    record Output(AccountId accountId) { }

}
