package org.mojave.core.settlement.contract.command.record;

import org.mojave.common.datatype.identifier.settlement.SettlementRecordId;

import java.time.Instant;

public interface HandleSettlementCompletionCommand {

    Output execute(Input input);

    record Input(SettlementRecordId settlementRecordId, Instant completedAt) { }

    record Output(SettlementRecordId settlementRecordId) { }

}
