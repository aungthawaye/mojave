package org.mojave.core.settlement.contract.command.record;

import org.mojave.common.datatype.identifier.settlement.SettlementBatchId;
import org.mojave.common.datatype.identifier.settlement.SettlementId;
import org.mojave.common.datatype.identifier.settlement.SettlementRecordId;

import java.time.Instant;

public interface HandleSettlementPreparationCommand {

    Output execute(Input input);

    record Input(SettlementRecordId settlementRecordId,
                 SettlementId settlementId,
                 SettlementBatchId settlementBatchId,
                 Instant preparedAt) { }

    record Output(SettlementRecordId settlementRecordId) { }

}
