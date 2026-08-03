package org.mojave.core.settlement.contract.command.window;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.identifier.participant.FspGroupId;
import org.mojave.scheme.rule.identifier.settlement.SettlementModelId;
import org.mojave.scheme.rule.identifier.settlement.SettlementWindowId;

import java.time.Instant;

public interface OpenSettlementWindowCommand {

    String SUBJECT_NAME = "sub-settlement.open-settlement-window-command";

    String TOPIC_NAME = "tp-settlement.open-settlement-window-command";

    Output execute(Input input);

    record Input(@JsonProperty(required = true) @NotNull SettlementModelId settlementModelId,
                 @JsonProperty(required = true) @NotNull Currency currency,
                 @JsonProperty(required = true) @NotNull FspGroupId fspGroupId,
                 @JsonProperty(required = true) @NotNull Instant periodStartAt) { }

    record Output(SettlementWindowId settlementWindowId) { }

}
