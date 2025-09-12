package io.mojaloop.core.account.contract.command.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import io.mojaloop.core.common.datatype.type.account.AccountCode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public interface ChangeAccountPropertiesCommand {

    Output execute(Input input);

    /**
     * Input for changing Account properties. Only allows changing code, name, and description.
     */
    record Input(
        @JsonProperty(required = true) @NotNull AccountId accountId,
        @JsonProperty(required = false) AccountCode code,
        @JsonProperty(required = false) @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String name,
        @JsonProperty(required = false) @Size(max = StringSizeConstraints.MAX_DESCRIPTION_LENGTH) String description
    ) { }

    record Output(AccountId accountId) { }

}
