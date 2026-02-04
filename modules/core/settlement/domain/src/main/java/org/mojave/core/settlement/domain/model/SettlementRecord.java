package org.mojave.core.settlement.domain.model;

import org.mojave.common.datatype.enums.settlement.SettlementType;
import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.identifier.participant.SspId;
import org.mojave.common.datatype.identifier.settlement.SettlementBatchId;
import org.mojave.common.datatype.identifier.settlement.SettlementId;
import org.mojave.common.datatype.identifier.settlement.SettlementRecordId;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.transfer.TransferId;

import java.math.BigDecimal;
import java.time.Instant;

public class SettlementRecord {

    protected SettlementRecordId id;

    protected SettlementType type;

    protected SettlementId settlementId;

    protected SettlementBatchId settlementBatchId;

    protected FspId payerFspId;

    protected FspId payeeFspId;

    protected Currency currency;

    protected BigDecimal amount;

    protected TransferId transferId;

    protected TransactionId transactionId;

    protected Instant transactionAt;

    protected SspId settlementProviderId;

    protected Instant initiatedAt;

    protected Instant preparedAt;

    protected Instant completedAt;

}
