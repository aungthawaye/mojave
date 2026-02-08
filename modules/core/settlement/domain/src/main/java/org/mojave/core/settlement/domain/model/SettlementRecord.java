package org.mojave.core.settlement.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.mojave.common.datatype.converter.identifier.participant.FspIdJavaType;
import org.mojave.common.datatype.converter.identifier.participant.SspIdJavaType;
import org.mojave.common.datatype.converter.identifier.settlement.SettlementBatchIdJavaType;
import org.mojave.common.datatype.converter.identifier.settlement.SettlementIdJavaType;
import org.mojave.common.datatype.converter.identifier.settlement.SettlementRecordIdJavaType;
import org.mojave.common.datatype.converter.identifier.transaction.TransactionIdJavaType;
import org.mojave.common.datatype.converter.identifier.transfer.TransferIdJavaType;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.settlement.SettlementType;
import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.identifier.participant.SspId;
import org.mojave.common.datatype.identifier.settlement.SettlementBatchId;
import org.mojave.common.datatype.identifier.settlement.SettlementId;
import org.mojave.common.datatype.identifier.settlement.SettlementRecordId;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.transfer.TransferId;
import org.mojave.component.jpa.JpaEntity;
import org.mojave.component.jpa.JpaInstantConverter;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.component.misc.data.DataConversion;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.core.settlement.contract.data.SettlementRecordData;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "stm_settlement_record",
    indexes = {
        @Index(
            name = "stm_settlement_record_01_IDX",
            columnList = "transaction_id, transfer_id"),
        @Index(
            name = "stm_settlement_record_02_IDX",
            columnList = "settlement_id, settlement_batch_id"),
        @Index(
            name = "stm_settlement_record_03_IDX",
            columnList = "payer_fsp_id, payee_fsp_id, currency")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SettlementRecord extends JpaEntity<SettlementRecordId>
    implements DataConversion<SettlementRecordData> {

    @Id
    @JavaType(SettlementRecordIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "settlement_record_id",
        nullable = false,
        updatable = false)
    protected SettlementRecordId id;

    @Column(
        name = "settlement_type",
        nullable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected SettlementType type;

    @JavaType(SettlementIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "settlement_id")
    protected SettlementId settlementId;

    @JavaType(SettlementBatchIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "settlement_batch_id")
    protected SettlementBatchId settlementBatchId;

    @JavaType(FspIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "payer_fsp_id",
        nullable = false)
    protected FspId payerFspId;

    @JavaType(FspIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "payee_fsp_id",
        nullable = false)
    protected FspId payeeFspId;

    @Column(
        name = "currency",
        nullable = false,
        length = StringSizeConstraints.MAX_CURRENCY_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Currency currency;

    @Column(
        name = "amount",
        nullable = false)
    protected BigDecimal amount;

    @JavaType(TransferIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "transfer_id")
    protected TransferId transferId;

    @JavaType(TransactionIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "transaction_id")
    protected TransactionId transactionId;

    @Column(name = "transaction_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant transactionAt;

    @JavaType(SspIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "settlement_provider_id",
        nullable = false)
    protected SspId settlementProviderId;

    @Column(name = "initiated_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant initiatedAt;

    @Column(name = "prepared_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant preparedAt;

    @Column(name = "completed_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant completedAt;

    public SettlementRecord(final SettlementType type,
                            final FspId payerFspId,
                            final FspId payeeFspId,
                            final Currency currency,
                            final BigDecimal amount,
                            final TransferId transferId,
                            final TransactionId transactionId,
                            final Instant transactionAt,
                            final SspId settlementProviderId) {

        Objects.requireNonNull(type);
        Objects.requireNonNull(payerFspId);
        Objects.requireNonNull(payeeFspId);
        Objects.requireNonNull(currency);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(settlementProviderId);

        this.id = new SettlementRecordId(Snowflake.get().nextId());
        this.type = type;
        this.payerFspId = payerFspId;
        this.payeeFspId = payeeFspId;
        this.currency = currency;
        this.amount = amount;
        this.transferId = transferId;
        this.transactionId = transactionId;
        this.transactionAt = transactionAt;
        this.settlementProviderId = settlementProviderId;
        this.initiatedAt = Instant.now();
    }

    @Override
    public SettlementRecordData convert() {

        return new SettlementRecordData(
            this.id, this.type, this.settlementId, this.settlementBatchId, this.payerFspId,
            this.payeeFspId, this.currency, this.amount, this.transferId, this.transactionId,
            this.transactionAt, this.settlementProviderId, this.initiatedAt, this.preparedAt,
            this.completedAt);
    }

    @Override
    public SettlementRecordId getId() {

        return id;
    }

    public void markCompleted(final Instant completedAt) {

        this.completedAt = completedAt == null ? Instant.now() : completedAt;
    }

    public void markInitiated(final SettlementId settlementId,
                              final SettlementBatchId settlementBatchId,
                              final Instant initiatedAt) {

        if (settlementId != null) {
            this.settlementId = settlementId;
        }

        if (settlementBatchId != null) {
            this.settlementBatchId = settlementBatchId;
        }

        this.initiatedAt = initiatedAt == null ? Instant.now() : initiatedAt;
    }

    public void markPrepared(final SettlementId settlementId,
                             final SettlementBatchId settlementBatchId,
                             final Instant preparedAt) {

        if (settlementId != null) {
            this.settlementId = settlementId;
        }

        if (settlementBatchId != null) {
            this.settlementBatchId = settlementBatchId;
        }

        this.preparedAt = preparedAt == null ? Instant.now() : preparedAt;
    }

}
