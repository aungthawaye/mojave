package org.mojave.core.settlement.contract.query;

import org.mojave.common.datatype.identifier.settlement.SettlementRecordId;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.core.settlement.contract.data.SettlementRecordData;

import java.util.List;

public interface SettlementRecordQuery {

    SettlementRecordData get(SettlementRecordId settlementRecordId);

    List<SettlementRecordData> get(TransactionId transactionId);

    List<SettlementRecordData> getAll();

}
