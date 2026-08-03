package org.mojave.core.settlement.contract.command.window;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.mojave.scheme.rule.identifier.settlement.SettlementWindowId;

import java.time.Instant;

public interface CloseSettlementWindowCommand {

    String SUBJECT_NAME = "sub-settlement.close-settlement-window-command";

    String TOPIC_NAME = "tp-settlement.close-settlement-window-command";

    Output execute(Input input);

    record Input(@JsonProperty(required = true) @NotNull SettlementWindowId settlementWindowId,
                 @JsonProperty(required = true) @NotNull Instant periodEndAt) { }

    record Output(SettlementWindowId settlementWindowId,
                  SettlementWindowId newSettlementWindowId) { }

}
