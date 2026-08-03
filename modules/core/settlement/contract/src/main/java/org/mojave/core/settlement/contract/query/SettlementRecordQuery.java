package org.mojave.core.settlement.contract.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.mojave.core.settlement.contract.data.SettlementRecordData;
import org.mojave.core.settlement.contract.exception.record.SettlementRecordNotFoundException;
import org.mojave.scheme.rule.identifier.settlement.SettlementRecordId;
import org.mojave.scheme.rule.identifier.settlement.SettlementWindowId;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;

import java.util.List;

public interface SettlementRecordQuery {

    String GET_BY_ID_SUBJECT_NAME = "sub-settlement.settlement-record-query.get-by-id";

    String GET_BY_TRANSACTION_ID_SUBJECT_NAME = "sub-settlement.settlement-record-query.get-by-transaction-id";

    String GET_BY_WINDOW_ID_SUBJECT_NAME = "sub-settlement.settlement-record-query.get-by-window-id";

    String GET_ALL_SUBJECT_NAME = "sub-settlement.settlement-record-query.get-all";

    SettlementRecordData get(SettlementRecordId settlementRecordId) throws SettlementRecordNotFoundException;

    SettlementRecordData get(TransactionId transactionId) throws SettlementRecordNotFoundException;

    List<SettlementRecordData> get(SettlementWindowId settlementWindowId);

    List<SettlementRecordData> getAll();

    record GetByIdInput(@JsonProperty(required = true) @NotNull SettlementRecordId settlementRecordId) { }

    record GetByTransactionIdInput(@JsonProperty(required = true) @NotNull TransactionId transactionId) { }

    record GetByWindowIdInput(@JsonProperty(required = true) @NotNull SettlementWindowId settlementWindowId) { }

    record GetAllInput() { }

}
