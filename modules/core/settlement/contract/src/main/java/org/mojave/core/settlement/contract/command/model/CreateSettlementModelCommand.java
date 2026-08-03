package org.mojave.core.settlement.contract.command.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.scheme.rule.enums.settlement.SettlementMethod;
import org.mojave.scheme.rule.identifier.participant.SspId;
import org.mojave.scheme.rule.identifier.settlement.SettlementModelId;

public interface CreateSettlementModelCommand {

    String SUBJECT_NAME = "sub-settlement.create-settlement-model-command";

    String TOPIC_NAME = "tp-settlement.create-settlement-model-command";

    Output execute(Input input);

    record Input(@JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String name,
                 @JsonProperty(required = true) @NotNull SettlementMethod settlementMethod,
                 @JsonProperty(required = true) @NotNull SspId sspId) { }

    record Output(SettlementModelId settlementModelId) { }

}
