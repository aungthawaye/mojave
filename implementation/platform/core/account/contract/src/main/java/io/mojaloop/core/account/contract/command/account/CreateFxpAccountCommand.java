package io.mojaloop.core.account.contract.command.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.enums.account.OverdraftMode;
import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import io.mojaloop.core.common.datatype.identifier.account.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.participant.FxpId;
import io.mojaloop.core.common.datatype.identifier.participant.HubId;
import io.mojaloop.core.common.datatype.type.account.AccountCode;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public interface CreateFxpAccountCommand {

    Output execute(Input input);

    /**
     * Input for creating an Account. Note: where the Account constructor in the domain would accept
     * other model instances (e.g., ChartEntry, Owner), here we accept only their Ids or primitive
     * representations to keep the contract decoupled from domain models, as requested.
     */
    record Input(
        @JsonProperty(required = true) @NotNull ChartEntryId chartEntryId,
        @JsonProperty(required = true) @NotNull FxpId fxpId,
        @JsonProperty(required = true) @NotNull @NotBlank Currency currency,
        @JsonProperty(required = true) @NotNull AccountCode code,
        @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String name,
        @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_DESCRIPTION_LENGTH) String description,
        @JsonProperty(required = true) @NotNull OverdraftMode overdraftMode,
        @JsonProperty(required = true) @NotNull BigDecimal overdraftLimit
    ) { }

    record Output(AccountId accountId) { }

}
