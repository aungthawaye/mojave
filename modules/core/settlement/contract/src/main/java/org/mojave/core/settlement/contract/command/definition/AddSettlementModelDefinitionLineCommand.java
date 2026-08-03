package org.mojave.core.settlement.contract.command.definition;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.settlement.LiquidityDirection;
import org.mojave.scheme.rule.identifier.settlement.SettlementDefinitionId;
import org.mojave.scheme.rule.identifier.settlement.SettlementModelDefinitionLineId;

public interface AddSettlementModelDefinitionLineCommand {

    String SUBJECT_NAME = "sub-settlement.add-settlement-model-definition-line-command";

    String TOPIC_NAME = "tp-settlement.add-settlement-model-definition-line-command";

    Output execute(Input input);

    record Input(@JsonProperty(required = true) @NotNull SettlementDefinitionId settlementModelDefinitionId,
                 @JsonProperty(required = true) @NotNull Integer step,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String participant,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String amountName,
                 @JsonProperty(required = true) @NotNull Currency currency,
                 @JsonProperty(required = true) @NotNull LiquidityDirection direction,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_DESCRIPTION_LENGTH) String description) { }

    record Output(SettlementDefinitionId settlementModelDefinitionId,
                  SettlementModelDefinitionLineId settlementModelDefinitionLineId) { }

}
