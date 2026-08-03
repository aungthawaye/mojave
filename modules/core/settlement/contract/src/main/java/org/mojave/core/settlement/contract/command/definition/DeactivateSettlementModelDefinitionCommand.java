package org.mojave.core.settlement.contract.command.definition;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.mojave.scheme.rule.identifier.settlement.SettlementDefinitionId;

public interface DeactivateSettlementModelDefinitionCommand {

    String SUBJECT_NAME = "sub-settlement.deactivate-settlement-model-definition-command";

    String TOPIC_NAME = "tp-settlement.deactivate-settlement-model-definition-command";

    Output execute(Input input);

    record Input(@JsonProperty(required = true) @NotNull SettlementDefinitionId settlementModelDefinitionId) { }

    record Output(SettlementDefinitionId settlementModelDefinitionId) { }

}
