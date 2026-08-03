package org.mojave.core.settlement.contract.command.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.mojave.scheme.rule.identifier.settlement.SettlementModelId;

public interface DeactivateSettlementModelCommand {

    String SUBJECT_NAME = "sub-settlement.deactivate-settlement-model-command";

    String TOPIC_NAME = "tp-settlement.deactivate-settlement-model-command";

    Output execute(Input input);

    record Input(@JsonProperty(required = true) @NotNull SettlementModelId settlementModelId) { }

    record Output(SettlementModelId settlementModelId) { }

}
