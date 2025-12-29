/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===
 */

package org.mojave.core.transfer.domain.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.mojave.component.jpa.JpaEntity;
import org.mojave.component.jpa.JpaInstantConverter;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.component.misc.data.DataConversion;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.core.common.datatype.converter.identifier.participant.FspIdJavaType;
import org.mojave.core.common.datatype.converter.identifier.transaction.TransactionIdJavaType;
import org.mojave.core.common.datatype.converter.identifier.transfer.TransferIdJavaType;
import org.mojave.core.common.datatype.converter.identifier.transfer.UdfTransferIdJavaType;
import org.mojave.core.common.datatype.converter.identifier.wallet.PositionUpdateIdJavaType;
import org.mojave.core.common.datatype.enums.Direction;
import org.mojave.core.common.datatype.enums.transfer.AbortReason;
import org.mojave.core.common.datatype.enums.transfer.DisputeReason;
import org.mojave.core.common.datatype.enums.transfer.TransferStatus;
import org.mojave.core.common.datatype.identifier.participant.FspId;
import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.core.common.datatype.identifier.transfer.TransferId;
import org.mojave.core.common.datatype.identifier.transfer.UdfTransferId;
import org.mojave.core.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.core.transfer.contract.data.TransferData;
import org.mojave.scheme.fspiop.core.Currency;
import org.mojave.scheme.fspiop.core.Money;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "tfr_transfer",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "tfr_transfer_01_UK",
            columnNames = {"transaction_id"}),
        @UniqueConstraint(
            name = "tfr_transfer_02_UK",
            columnNames = {"udf_transfer_id"}),
        @UniqueConstraint(
            name = "tfr_transfer_03_UK",
            columnNames = {"reservation_id"}),
        @UniqueConstraint(
            name = "tfr_transfer_04_UK",
            columnNames = {"payer_commit_id"}),
        @UniqueConstraint(
            name = "tfr_transfer_05_UK",
            columnNames = {"payee_commit_id"}),
        @UniqueConstraint(
            name = "tfr_transfer_06_UK",
            columnNames = {"rollback_id"}),
        @UniqueConstraint(
            name = "tfr_transfer_07_UK",
            columnNames = {"ilp_fulfilment"})},
    indexes = {
        @Index(
            name = "tfr_transfer_01_IDX",
            columnList = "payer_fsp_id, payee_fsp_id, udf_transfer_id"),
        @Index(
            name = "tfr_transfer_02_IDX",
            columnList = "payer_fsp_id"),
        @Index(
            name = "tfr_transfer_03_IDX",
            columnList = "payer_fsp_id, transaction_id"),
        @Index(
            name = "tfr_transfer_04_IDX",
            columnList = "payee_fsp_id"),
        @Index(
            name = "tfr_transfer_05_IDX",
            columnList = "payee_fsp_id, transaction_id"),
        @Index(
            name = "tfr_transfer_06_IDX",
            columnList = "payee_fsp_id, payer_fsp_id"),
        @Index(
            name = "tfr_transfer_07_IDX",
            columnList = "transaction_at"),
        @Index(
            name = "tfr_transfer_08_IDX",
            columnList = "payer_party_id"),
        @Index(
            name = "tfr_transfer_09_IDX",
            columnList = "payee_party_id"),
        @Index(
            name = "tfr_transfer_10_IDX",
            columnList = "transfer_currency"),
        @Index(
            name = "tfr_transfer_11_IDX",
            columnList = "reservation_timeout_at"),
        @Index(
            name = "tfr_transfer_12_IDX",
            columnList = "reserved_at"),
        @Index(
            name = "tfr_transfer_13_IDX",
            columnList = "committed_at"),
        @Index(
            name = "tfr_transfer_14_IDX",
            columnList = "aborted_at"),
        @Index(
            name = "tfr_transfer_15_IDX",
            columnList = "status")})
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Transfer extends JpaEntity<TransferId> implements DataConversion<TransferData> {

    @Id
    @JavaType(TransferIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "transfer_id")
    protected TransferId id;

    @Basic
    @JavaType(TransactionIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "transaction_id",
        nullable = false,
        updatable = false)
    protected TransactionId transactionId;

    @Column(
        name = "transaction_at",
        nullable = false,
        updatable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant transactionAt;

    @Basic
    @JavaType(UdfTransferIdJavaType.class)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(
        name = "udf_transfer_id",
        nullable = false,
        updatable = false,
        length = StringSizeConstraints.MAX_UDF_TRANSFER_ID_LENGTH)
    protected UdfTransferId udfTransferId;

    @Basic
    @JavaType(FspIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "payer_fsp_id",
        nullable = false,
        updatable = false)
    protected FspId payerFspId;

    @Embedded
    @AttributeOverrides(
        value = {
            @AttributeOverride(
                name = "partyIdType",
                column = @Column(
                    name = "payer_party_type",
                    length = StringSizeConstraints.MAX_ENUM_LENGTH)),
            @AttributeOverride(
                name = "partyId",
                column = @Column(
                    name = "payer_party_id",
                    length = 48)),
            @AttributeOverride(
                name = "subId",
                column = @Column(
                    name = "payer_sub_id",
                    length = 48))})
    protected Party payer;

    @Basic
    @JavaType(FspIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "payee_fsp_id",
        nullable = false,
        updatable = false)
    protected FspId payeeFspId;

    @Embedded
    @AttributeOverrides(
        value = {
            @AttributeOverride(
                name = "partyIdType",
                column = @Column(
                    name = "payee_party_type",
                    length = StringSizeConstraints.MAX_ENUM_LENGTH)),
            @AttributeOverride(
                name = "partyId",
                column = @Column(
                    name = "payee_party_id",
                    length = 48)),
            @AttributeOverride(
                name = "subId",
                column = @Column(
                    name = "payee_sub_id",
                    length = 48))})
    protected Party payee;

    @Column(
        name = "transfer_currency",
        nullable = false,
        updatable = false,
        length = StringSizeConstraints.MAX_CURRENCY_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Currency transferCurrency;

    @Column(
        name = "transfer_amount",
        nullable = false,
        updatable = false,
        precision = 34,
        scale = 4)
    protected BigDecimal transferAmount;

    @Column(
        name = "payee_fsp_fee_currency",
        nullable = false,
        updatable = false,
        length = StringSizeConstraints.MAX_CURRENCY_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Currency payeeFspFeeCurrency;

    @Column(
        name = "payee_fsp_fee_amount",
        updatable = false,
        precision = 34,
        scale = 4)
    protected BigDecimal payeeFspFeeAmount;

    @Column(
        name = "payee_fsp_commission_currency",
        nullable = false,
        updatable = false,
        length = StringSizeConstraints.MAX_CURRENCY_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Currency payeeFspCommissionCurrency;

    @Column(
        name = "payee_fsp_commission_amount",
        updatable = false,
        precision = 34,
        scale = 4)
    protected BigDecimal payeeFspCommissionAmount;

    @Column(
        name = "payee_receive_currency",
        nullable = false,
        updatable = false,
        length = StringSizeConstraints.MAX_CURRENCY_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Currency payeeReceiveCurrency;

    @Column(
        name = "payee_receive_amount",
        updatable = false,
        precision = 34,
        scale = 4)
    protected BigDecimal payeeReceiveAmount;

    @Column(name = "request_expiration")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant requestExpiration;

    @Basic
    @JavaType(PositionUpdateIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "reservation_id",
        unique = true)
    protected PositionUpdateId reservationId;

    @Column(
        name = "status",
        nullable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TransferStatus status;

    @Column(
        name = "received_at",
        nullable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant receivedAt;

    @Column(name = "reserved_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant reservedAt;

    @Column(name = "committed_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant committedAt;

    @Column(name = "aborted_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant abortedAt;

    @Column(
        name = "abort_reason",
        length = StringSizeConstraints.MAX_REASON_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected AbortReason abortReason;

    @Column(name = "dispute")
    protected Boolean dispute = false;

    @Column(name = "dispute_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant disputeAt;

    @Column(
        name = "dispute_reason",
        length = StringSizeConstraints.MAX_REASON_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected DisputeReason disputeReason;

    @Column(name = "dispute_resolved")
    protected Boolean disputeResolved;

    @Column(name = "dispute_resolved_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant disputeResolvedAt;

    @Column(name = "reservation_timeout_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant reservationTimeoutAt;

    @Column(name = "payee_completed_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant payeeCompletedAt;

    @Getter(AccessLevel.NONE)
    @OneToMany(
        mappedBy = "transfer",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY)
    protected List<TransferExtension> extensions = new ArrayList<>();

    @Column(
        name = "ilp_fulfilment",
        length = StringSizeConstraints.MAX_ILP_PACKET_FULFILMENT_LENGTH)
    protected String ilpFulfilment;

    @OneToOne(
        mappedBy = "transfer",
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    protected TransferIlpPacket ilpPacket;

    public Transfer(TransactionId transactionId,
                    Instant transactionAt,
                    UdfTransferId udfTransferId,
                    FspId payerFspId,
                    Party payer,
                    FspId payeeFspId,
                    Party payee,
                    Money transferAmount,
                    Money payeeFspFee,
                    Money payeeFspCommission,
                    Money payeeReceiveAmount,
                    String ilpPacket,
                    String ilpCondition,
                    Instant requestExpiration,
                    Instant reservationTimeoutAt) {

        assert transactionId != null;
        assert transactionAt != null;
        assert udfTransferId != null;
        assert payerFspId != null;
        assert payer != null;
        assert payeeFspId != null;
        assert payee != null;
        assert transferAmount != null;
        assert payeeFspFee != null;
        assert payeeFspCommission != null;
        assert payeeReceiveAmount != null;
        assert ilpPacket != null;
        assert ilpCondition != null;
        assert reservationTimeoutAt != null;

        this.id = new TransferId(Snowflake.get().nextId());
        this.transactionId = transactionId;
        this.transactionAt = transactionAt;
        this.udfTransferId = udfTransferId;
        this.payerFspId = payerFspId;
        this.payer = payer;
        this.payeeFspId = payeeFspId;
        this.payee = payee;

        this.transferCurrency = transferAmount.getCurrency();
        this.transferAmount = new BigDecimal(transferAmount.getAmount());

        this.payeeFspFeeCurrency = payeeFspFee.getCurrency();
        this.payeeFspFeeAmount = new BigDecimal(payeeFspFee.getAmount());

        this.payeeFspCommissionCurrency = payeeFspCommission.getCurrency();
        this.payeeFspCommissionAmount = new BigDecimal(payeeFspCommission.getAmount());

        this.payeeReceiveCurrency = payeeReceiveAmount.getCurrency();
        this.payeeReceiveAmount = new BigDecimal(payeeReceiveAmount.getAmount());

        this.requestExpiration = requestExpiration;
        this.status = TransferStatus.RECEIVED;
        this.receivedAt = Instant.now();
        this.reservationTimeoutAt = reservationTimeoutAt;

        this.ilpPacket = new TransferIlpPacket(this, ilpPacket, ilpCondition);
    }

    public void aborted(AbortReason abortReason) {

        this.abortReason = abortReason;

        this.status = TransferStatus.ABORTED;
        this.abortedAt = Instant.now();
    }

    public void addExtension(Direction direction, String key, String value) {

        this.extensions.add(new TransferExtension(this, direction, key, value));
    }

    public void committed(String ilpFulfilment, Instant completedAt) {

        assert ilpFulfilment != null;

        this.status = TransferStatus.COMMITTED;

        this.committedAt = Instant.now();
        this.payeeCompletedAt = completedAt;

        this.ilpFulfilment = ilpFulfilment;
    }

    @Override
    public TransferData convert() {

        final var payerData = new TransferData.PartyData(
            this.payer.partyIdType(), this.payer.partyId(), this.payer.subId());
        final var payeeData = new TransferData.PartyData(
            this.payee.partyIdType(), this.payee.partyId(), this.payee.subId());

        final var extData = this.extensions
                                .stream()
                                .map(x -> new TransferData.TransferExtensionData(
                                    x.getDirection(),
                                    x.getKey(), x.getValue()))
                                .toList();

        final var ilpData = this.ilpPacket == null ? null : new TransferData.TransferIlpPacketData(
            this.ilpPacket.getIlpPacket(), this.ilpPacket.getCondition());

        return new TransferData(
            this.id, this.transactionId, this.transactionAt, this.udfTransferId, this.payerFspId,
            payerData, this.payeeFspId, payeeData, this.transferCurrency, this.transferAmount,
            this.requestExpiration, this.reservationId, this.status, this.receivedAt,
            this.reservedAt, this.committedAt, this.abortedAt, this.abortReason, this.disputeAt,
            this.disputeReason, this.reservationTimeoutAt, this.payeeCompletedAt, extData,
            this.ilpFulfilment, ilpData);
    }

    public void disputed(DisputeReason disputeReason) {

        assert disputeReason != null;

        this.disputeReason = disputeReason;
        this.disputeAt = Instant.now();

        this.dispute = true;
    }

    public List<TransferExtension> getExtensions() {

        return Collections.unmodifiableList(this.extensions);
    }

    @Override
    public TransferId getId() {

        return this.id;
    }

    public void reserved(PositionUpdateId reservationId) {

        assert reservationId != null;

        this.status = TransferStatus.RESERVED;
        this.reservedAt = Instant.now();
        this.reservationId = reservationId;
    }

    public void resolvedDispute() {

        this.disputeResolved = true;
        this.disputeResolvedAt = Instant.now();
    }

}
