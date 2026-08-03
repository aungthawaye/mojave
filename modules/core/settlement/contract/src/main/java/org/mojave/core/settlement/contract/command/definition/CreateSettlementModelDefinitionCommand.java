package org.mojave.core.settlement.contract.command.definition;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.settlement.LiquidityDirection;
import org.mojave.scheme.rule.enums.settlement.TransactionPeriod;
import org.mojave.scheme.rule.identifier.participant.FspGroupId;
import org.mojave.scheme.rule.identifier.settlement.SettlementDefinitionId;
import org.mojave.scheme.rule.identifier.settlement.SettlementModelDefinitionLineId;
import org.mojave.scheme.rule.identifier.settlement.SettlementModelId;
import org.mojave.scheme.rule.scenario.ScenarioType;

import java.util.List;

public interface CreateSettlementModelDefinitionCommand {

    String SUBJECT_NAME = "sub-settlement.create-settlement-model-definition-command";

    String TOPIC_NAME = "tp-settlement.create-settlement-model-definition-command";

    Output execute(Input input);

    record Input(@JsonProperty(required = true) @NotNull ScenarioType scenario,
                 @JsonProperty(required = true) @NotNull Currency currency,
                 @JsonProperty(required = true) @NotNull FspGroupId fspGroupId,
                 @JsonProperty(required = true) @NotNull TransactionPeriod transactionPeriod,
                 @JsonProperty(required = true) @NotNull SettlementModelId settlementModelId,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String name,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_DESCRIPTION_LENGTH) String description,
                 @JsonProperty(required = true) List<SettlementModelDefinitionLine> settlementModelDefinitionLines) {

        public record SettlementModelDefinitionLine(
            @JsonProperty(required = true) @NotNull Integer step,
            @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String participant,
            @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String amountName,
            @JsonProperty(required = true) @NotNull Currency currency,
            @JsonProperty(required = true) @NotNull LiquidityDirection direction,
            @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_DESCRIPTION_LENGTH) String description) { }

    }

    record Output(SettlementDefinitionId settlementModelDefinitionId,
                  List<SettlementModelDefinitionLineId> settlementModelDefinitionLineIds) { }

}
