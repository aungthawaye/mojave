package org.mojave.core.settlement.domain.model;

import org.mojave.scheme.common.datatype.identifier.participant.FspId;
import org.mojave.scheme.common.datatype.identifier.participant.SspId;
import org.mojave.scheme.common.datatype.identifier.settlement.SettlementBatchId;
import org.mojave.scheme.common.datatype.identifier.settlement.SettlementId;
import org.mojave.scheme.common.datatype.identifier.settlement.SettlementRecordId;
import org.mojave.scheme.common.datatype.identifier.transaction.TransactionId;
import org.mojave.scheme.fspiop.core.Currency;

import java.math.BigDecimal;
import java.time.Instant;

public class SettlementRecord {

    protected SettlementRecordId id;

    protected SettlementId settlementId;

    protected SettlementBatchId settlementBatchId;

    protected FspId receivingFspId;

    protected Currency currency;

    protected BigDecimal amount;

    protected TransactionId transactionId;

    protected Instant transactionAt;

    protected SspId settledBy;

    protected Instant initiatedAt;

    protected Instant preparedAt;

    protected Instant settledAt;

}
