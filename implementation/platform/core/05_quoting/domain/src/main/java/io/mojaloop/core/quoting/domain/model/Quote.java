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

package io.mojaloop.core.quoting.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.jpa.JpaInstantConverter;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.converter.identifier.participant.FspIdJavaType;
import io.mojaloop.core.common.datatype.converter.identifier.quoting.QuoteIdJavaType;
import io.mojaloop.core.common.datatype.converter.identifier.quoting.UdfQuoteIdJavaType;
import io.mojaloop.core.common.datatype.enums.Direction;
import io.mojaloop.core.common.datatype.enums.quoting.QuotingStage;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.identifier.quoting.QuoteId;
import io.mojaloop.core.common.datatype.identifier.quoting.UdfQuoteId;
import io.mojaloop.core.quoting.contract.exception.ExpirationNotInFutureException;
import io.mojaloop.core.quoting.contract.exception.ReceivingAmountMismatchException;
import io.mojaloop.core.quoting.contract.exception.TransferAmountMismatchException;
import io.mojaloop.fspiop.component.handy.FspiopDates;
import io.mojaloop.fspiop.spec.core.AmountType;
import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.Money;
import io.mojaloop.fspiop.spec.core.QuotesIDPutResponse;
import io.mojaloop.fspiop.spec.core.TransactionInitiator;
import io.mojaloop.fspiop.spec.core.TransactionInitiatorType;
import io.mojaloop.fspiop.spec.core.TransactionScenario;
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
import java.util.Date;
import java.util.List;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(name = "qot_quote",
       uniqueConstraints = {@UniqueConstraint(name = "qot_udf_quote_id_UK", columnNames = {"udf_quote_id"})},
       indexes = {@Index(name = "qot_quote_requested_at_IDX", columnList = "requested_at"),
                  @Index(name = "qot_quote_responded_at_IDX", columnList = "responded_at"),
                  @Index(name = "qot_quote_currency_IDX", columnList = "currency"),
                  @Index(name = "qot_quote_amount_type_IDX", columnList = "amount_type"),
                  @Index(name = "qot_quote_payer_fsp_id_payee_fsp_id_IDX", columnList = "payer_fsp_id, payee_fsp_id")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quote extends JpaEntity<QuoteId> {

    @Id
    @JavaType(QuoteIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "quote_id", nullable = false, updatable = false)
    protected QuoteId id;

    @Basic
    @JavaType(FspIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "payer_fsp_id", nullable = false, updatable = false)
    protected FspId payerFspId;

    @Basic
    @JavaType(FspIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "payee_fsp_id", nullable = false, updatable = false)
    protected FspId payeeFspId;

    @Basic
    @JavaType(UdfQuoteIdJavaType.class)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "udf_quote_id", nullable = false, updatable = false, length = StringSizeConstraints.MAX_UDF_QUOTE_ID_LENGTH)
    protected UdfQuoteId udfQuoteId;

    @Column(name = "currency", nullable = false, updatable = false, length = StringSizeConstraints.MAX_CURRENCY_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Currency currency;

    @Column(name = "amount", nullable = false, updatable = false, precision = 34, scale = 4)
    protected BigDecimal amount;

    @Column(name = "fees", updatable = false, precision = 34, scale = 4)
    protected BigDecimal fees;

    @Column(name = "amount_type", nullable = false, updatable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected AmountType amountType;

    @Column(name = "scenario", nullable = false, updatable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TransactionScenario scenario;

    @Column(name = "sub_scenario", updatable = false, length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String subScenario;

    @Column(name = "initiator", nullable = false, updatable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TransactionInitiator initiator;

    @Column(name = "initiator_type", nullable = false, updatable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TransactionInitiatorType initiatorType;

    @Column(name = "request_expiration", updatable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant requestExpiration;

    @Embedded
    @AttributeOverrides(value = {@AttributeOverride(name = "partyIdType",
                                                    column = @Column(name = "payer_party_type", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)),
                                 @AttributeOverride(name = "partyId", column = @Column(name = "payer_party_id", nullable = false, length = 48)),
                                 @AttributeOverride(name = "subId", column = @Column(name = "payer_sub_id", length = 48))})
    protected Party payer;

    @Embedded
    @AttributeOverrides(value = {@AttributeOverride(name = "partyIdType",
                                                    column = @Column(name = "payee_party_type", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)),
                                 @AttributeOverride(name = "partyId", column = @Column(name = "payee_party_id", nullable = false, length = 48)),
                                 @AttributeOverride(name = "subId", column = @Column(name = "payee_sub_id", length = 48))})

    protected Party payee;

    @Column(name = "response_expiration")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant responseExpiration;

    @Column(name = "transfer_amount", nullable = false, precision = 34, scale = 4)
    protected BigDecimal transferAmount;

    @Column(name = "payee_fsp_fee", precision = 34, scale = 4)
    protected BigDecimal payeeFspFee;

    @Column(name = "payee_fsp_commission", precision = 34, scale = 4)
    protected BigDecimal payeeFspCommission;

    @Column(name = "payee_receive_amount", nullable = false, precision = 34, scale = 4)
    protected BigDecimal payeeReceiveAmount;

    @Column(name = "requested_at", nullable = false, updatable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant requestedAt;

    @Column(name = "responded_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant respondedAt;

    @Column(name = "stage", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected QuotingStage stage;

    @Column(name = "error", length = StringSizeConstraints.MAX_PARAGRAPH_LENGTH)
    protected String error;

    @Getter(AccessLevel.NONE)
    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<QuoteExtension> extensions = new ArrayList<>();

    @OneToOne(mappedBy = "quote", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    protected QuoteIlpPacket ilpPacket;

    public Quote(FspId payerFspId,
                 FspId payeeFspId,
                 UdfQuoteId udfQuoteId,
                 Currency currency,
                 BigDecimal amount,
                 BigDecimal fees,
                 AmountType amountType,
                 TransactionScenario scenario,
                 String subScenario,
                 TransactionInitiator initiator,
                 TransactionInitiatorType initiatorType,
                 Instant requestExpiration,
                 Party payer,
                 Party payee) throws ExpirationNotInFutureException {

        assert payerFspId != null;
        assert payeeFspId != null;
        assert udfQuoteId != null;
        assert currency != null;
        assert amount != null;
        assert amountType != null;
        assert scenario != null;
        assert initiator != null;
        assert initiatorType != null;
        assert payer != null;
        assert payee != null;

        this.id = new QuoteId(Snowflake.get().nextId());
        this.payerFspId = payerFspId;
        this.payeeFspId = payeeFspId;
        this.udfQuoteId = udfQuoteId;
        this.currency = currency;
        this.amount = amount;
        this.fees = fees;
        this.amountType = amountType;
        this.scenario = scenario;
        this.subScenario = subScenario;
        this.initiator = initiator;
        this.initiatorType = initiatorType;
        this.requestExpiration = requestExpiration;
        this.payer = payer;
        this.payee = payee;
        this.requestedAt = Instant.now();
        this.stage = QuotingStage.REQUESTED;
        this.ilpPacket = new QuoteIlpPacket(this);

        if (this.requestExpiration != null && this.requestExpiration.isBefore(Instant.now())) {

            throw new ExpirationNotInFutureException();
        }

    }

    public void addExtension(Direction direction, String key, String value) {

        this.extensions.add(new QuoteExtension(this, direction, key, value));
    }

    public void error(String reason) {

        this.error = reason;
        this.stage = QuotingStage.ERROR;
        this.respondedAt = Instant.now();
    }

    @Override
    public QuoteId getId() {

        return this.id;
    }

    public void responded(Instant responseExpiration,
                          BigDecimal transferAmount,
                          BigDecimal payeeFspFee,
                          BigDecimal payeeFspCommission,
                          BigDecimal payeeReceiveAmount,
                          String ilpPacket,
                          String condition) throws TransferAmountMismatchException, ReceivingAmountMismatchException, ExpirationNotInFutureException {

        assert transferAmount != null;
        assert payeeReceiveAmount != null;

        this.responseExpiration = responseExpiration;
        this.transferAmount = transferAmount;
        this.payeeFspFee = payeeFspFee;
        this.payeeFspCommission = payeeFspCommission;
        this.payeeReceiveAmount = payeeReceiveAmount;
        this.respondedAt = Instant.now();
        this.stage = QuotingStage.RESPONDED;

        if (this.responseExpiration != null && this.responseExpiration.isBefore(Instant.now())) {

            throw new ExpirationNotInFutureException();
        }

        if (this.amountType == AmountType.SEND) {

            if (transferAmount.subtract(this.amount).signum() != 0) {
                throw new TransferAmountMismatchException(transferAmount, this.amount, this.amountType);
            }

        } else if (this.amountType == AmountType.RECEIVE) {

            if (payeeReceiveAmount.subtract(this.amount).signum() != 0) {
                throw new ReceivingAmountMismatchException(payeeReceiveAmount, this.amount, this.amountType);
            }
        }

        this.ilpPacket.responded(ilpPacket, condition);

    }

    public QuotesIDPutResponse toFspiopResponse() {

        return new QuotesIDPutResponse(new Money(this.currency, this.transferAmount.stripTrailingZeros().toPlainString()),
                                       FspiopDates.forRequestBody(Date.from(this.responseExpiration)),
                                       this.ilpPacket.getIlpPacket(),
                                       this.ilpPacket.getCondition());
    }

    List<QuoteExtension> getExtensions() {

        return Collections.unmodifiableList(this.extensions);
    }

}
