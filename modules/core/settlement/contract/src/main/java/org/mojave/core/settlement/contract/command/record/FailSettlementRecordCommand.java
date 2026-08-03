package org.mojave.core.settlement.contract.command.record;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.mojave.scheme.rule.identifier.settlement.SettlementRecordId;

public interface FailSettlementRecordCommand {

    String SUBJECT_NAME = "sub-settlement.fail-settlement-record-command";

    String TOPIC_NAME = "tp-settlement.fail-settlement-record-command";

    Output execute(Input input);

    record Input(@JsonProperty(required = true) @NotNull SettlementRecordId settlementRecordId) { }

    record Output(SettlementRecordId settlementRecordId) { }

}
