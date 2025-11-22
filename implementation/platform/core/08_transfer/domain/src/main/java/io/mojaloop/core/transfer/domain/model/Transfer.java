/*-
 * ================================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
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
 * ================================================================================
 */

package io.mojaloop.core.transfer.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.jpa.JpaInstantConverter;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.converter.identifier.transaction.TransactionIdJavaType;
import io.mojaloop.core.common.datatype.converter.identifier.transfer.TransferIdJavaType;
import io.mojaloop.core.common.datatype.converter.identifier.transfer.UdfTransferIdJavaType;
import io.mojaloop.core.common.datatype.converter.identifier.wallet.PositionUpdateIdJavaType;
import io.mojaloop.core.common.datatype.converter.type.fspiop.FspCodeConverter;
import io.mojaloop.core.common.datatype.enums.Direction;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.transfer.TransferId;
import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.TransferState;
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
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(name = "tfr_transfer",
       uniqueConstraints = {@UniqueConstraint(name = "tfr_transfer_transaction_id_UK", columnNames = {"transaction_id"}),
                            @UniqueConstraint(name = "tfr_transfer_udf_transfer_id_UK", columnNames = {"udf_transfer_id"}),
                            @UniqueConstraint(name = "tfr_transfer_reservation_id_UK", columnNames = {"reservation_id"}),
                            @UniqueConstraint(name = "tfr_transfer_payer_commit_id_UK", columnNames = {"payer_commit_id"}),
                            @UniqueConstraint(name = "tfr_transfer_payee_commit_id_UK", columnNames = {"payee_commit_id"}),
                            @UniqueConstraint(name = "tfr_transfer_rollback_id_UK", columnNames = {"rollback_id"})},
       indexes = {@Index(name = "tfr_transfer_payer_fsp_IDX", columnList = "payer_fsp"),
                  @Index(name = "tfr_transfer_payer_fsp_transaction_id_IDX", columnList = "payer_fsp, transaction_id"),
                  @Index(name = "tfr_transfer_payee_fsp_IDX", columnList = "payee_fsp"),
                  @Index(name = "tfr_transfer_payee_fsp_transaction_IDX", columnList = "payee_fsp, transaction_id"),
                  @Index(name = "tfr_transfer_payee_fsp_payer_fsp_IDX", columnList = "payee_fsp, payer_fsp"),
                  @Index(name = "tfr_transfer_transaction_at_IDX", columnList = "transaction_at"),
                  @Index(name = "tfr_transfer_payer_party_id_IDX", columnList = "payer_party_id"),
                  @Index(name = "tfr_transfer_payee_party_id_IDX", columnList = "payee_party_id"),
                  @Index(name = "tfr_transfer_reservation_timeout_at_IDX", columnList = "reservation_timeout_at"),
                  @Index(name = "tfr_transfer_reserved_at_IDX", columnList = "reserved_at"),
                  @Index(name = "tfr_transfer_committed_at_IDX", columnList = "committed_at"),
                  @Index(name = "tfr_transfer_aborted_at_IDX", columnList = "aborted_at"),
                  @Index(name = "tfr_transfer_state_IDX", columnList = "state")})
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Transfer extends JpaEntity<TransferId> {

    @Id
    @JavaType(TransferIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "transfer_id")
    protected TransferId id;

    @Basic
    @JavaType(TransactionIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "transaction_id", nullable = false, updatable = false)
    protected TransactionId transactionId;

    @Column(name = "transaction_at", nullable = false, updatable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant transactionAt;

    @Basic
    @JavaType(UdfTransferIdJavaType.class)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "udf_transfer_id", nullable = false, updatable = false, length = StringSizeConstraints.MAX_UDF_TRANSFER_ID_LENGTH)
    protected UdfTransferId udfTransferId;

    @Column(name = "payer_fsp", nullable = false, updatable = false, length = StringSizeConstraints.MAX_CODE_LENGTH)
    @Convert(converter = FspCodeConverter.class)
    protected FspCode payerFsp;

    @Embedded
    @AttributeOverrides(value = {@AttributeOverride(name = "partyIdType", column = @Column(name = "payer_party_type", length = StringSizeConstraints.MAX_ENUM_LENGTH)),
                                 @AttributeOverride(name = "partyId", column = @Column(name = "payer_party_id", length = 48)),
                                 @AttributeOverride(name = "subId", column = @Column(name = "payer_sub_id", length = 48))})
    protected Party payer;

    @Column(name = "payee_fsp", nullable = false, updatable = false, length = StringSizeConstraints.MAX_CODE_LENGTH)
    @Convert(converter = FspCodeConverter.class)
    protected FspCode payeeFsp;

    @Embedded
    @AttributeOverrides(value = {@AttributeOverride(name = "partyIdType", column = @Column(name = "payee_party_type", length = StringSizeConstraints.MAX_ENUM_LENGTH)),
                                 @AttributeOverride(name = "partyId", column = @Column(name = "payee_party_id", length = 48)),
                                 @AttributeOverride(name = "subId", column = @Column(name = "payee_sub_id", length = 48))})

    protected Party payee;

    @Column(name = "currency", nullable = false, updatable = false, length = StringSizeConstraints.MAX_CURRENCY_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Currency currency;

    @Column(name = "transfer_amount", nullable = false, updatable = false, precision = 34, scale = 4)
    protected BigDecimal transferAmount;

    @Column(name = "request_expiration")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant requestExpiration;

    @Basic
    @JavaType(PositionUpdateIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "reservation_id", unique = true)
    protected PositionUpdateId reservationId;

    @Basic
    @JavaType(PositionUpdateIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "payer_commit_id", unique = true)
    protected PositionUpdateId payerCommitId;

    @Basic
    @JavaType(PositionUpdateIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "payee_commit_id", unique = true)
    protected PositionUpdateId payeeCommitId;

    @Basic
    @JavaType(PositionUpdateIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "rollback_id", unique = true)
    protected PositionUpdateId rollbackId;

    @Column(name = "state", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TransferState state;

    @Column(name = "received_at", nullable = false)
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

    @Column(name = "error")
    protected String error;

    @Column(name = "reservation_timeout_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant reservationTimeoutAt;

    @Column(name = "payee_completed_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant payeeCompletedAt;

    @Getter(AccessLevel.NONE)
    @OneToMany(mappedBy = "transfer", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<TransferExtension> extensions = new ArrayList<>();

    @OneToOne(mappedBy = "transfer", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    protected TransferIlpPacket ilpPacket;

    public Transfer(TransactionId transactionId,
                    Instant transactionAt,
                    UdfTransferId udfTransferId,
                    FspCode payerFsp,
                    Party payer,
                    FspCode payeeFsp,
                    Party payee,
                    Currency currency,
                    BigDecimal transferAmount,
                    String ilpPacket,
                    String ilpCondition,
                    Instant requestExpiration,
                    Instant reservationTimeoutAt) {

        assert transactionId != null;
        assert transactionAt != null;
        assert udfTransferId != null;
        assert payerFsp != null;
        assert payer != null;
        assert payeeFsp != null;
        assert payee != null;
        assert currency != null;
        assert transferAmount != null;
        assert ilpPacket != null;
        assert ilpCondition != null;
        assert reservationTimeoutAt != null;

        this.id = new TransferId(Snowflake.get().nextId());
        this.transactionId = transactionId;
        this.transactionAt = transactionAt;
        this.udfTransferId = udfTransferId;
        this.payerFsp = payerFsp;
        this.payer = payer;
        this.payeeFsp = payeeFsp;
        this.payee = payee;
        this.currency = currency;
        this.transferAmount = transferAmount;
        this.requestExpiration = requestExpiration;
        this.state = TransferState.RECEIVED;
        this.receivedAt = Instant.now();
        this.reservationTimeoutAt = reservationTimeoutAt;

        this.ilpPacket = new TransferIlpPacket(this, ilpPacket, ilpCondition);
    }

    public void aborted(PositionUpdateId rollbackId, String error) {

        this.rollbackId = rollbackId;
        this.state = TransferState.ABORTED;
        this.abortedAt = Instant.now();
        this.error = error;
    }

    public void addExtension(Direction direction, String key, String value) {

        this.extensions.add(new TransferExtension(this, direction, key, value));
    }

    public void committed(String ilpFulfilment, PositionUpdateId payerCommitId, PositionUpdateId payeeCommitId, Instant completedAt) {

        assert ilpFulfilment != null;
        assert payerCommitId != null;
        assert payeeCommitId != null;

        this.state = TransferState.COMMITTED;

        this.committedAt = Instant.now();
        this.payeeCompletedAt = completedAt;

        this.ilpPacket.fulfil(ilpFulfilment);

        this.payerCommitId = payerCommitId;
        this.payeeCommitId = payeeCommitId;
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

        this.state = TransferState.RESERVED;
        this.reservedAt = Instant.now();
        this.reservationId = reservationId;
    }

}
