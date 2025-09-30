package io.mojaloop.core.quoting.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.jpa.JpaInstantConverter;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.converter.identifier.quoting.QuoteIdJavaType;
import io.mojaloop.core.common.datatype.converter.identifier.quoting.UdfQuoteIdJavaType;
import io.mojaloop.core.common.datatype.enums.quoting.QuotingStage;
import io.mojaloop.core.common.datatype.identifier.quoting.QuoteId;
import io.mojaloop.core.common.datatype.identifier.quoting.UdfQuoteId;
import io.mojaloop.core.quoting.contract.exception.ExpirationNotInFutureException;
import io.mojaloop.core.quoting.contract.exception.ReceivingAmountMismatchException;
import io.mojaloop.core.quoting.contract.exception.TransferAmountMismatchException;
import io.mojaloop.fspiop.spec.core.AmountType;
import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.TransactionInitiator;
import io.mojaloop.fspiop.spec.core.TransactionInitiatorType;
import io.mojaloop.fspiop.spec.core.TransactionScenario;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(name = "qot_quote", uniqueConstraints = {@UniqueConstraint(name = "qot_udf_quote_id_UK", columnNames = {"udf_quote_id"})},
       indexes = {@Index(name = "qot_quote_requested_at_IDX", columnList = "requested_at"), @Index(name = "qot_quote_responded_at_IDX", columnList = "responded_at"),
                  @Index(name = "qot_quote_currency_IDX", columnList = "currency"), @Index(name = "qot_quote_amount_type_IDX", columnList = "amount_type")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quote extends JpaEntity<QuoteId> {

    @Id
    @JavaType(QuoteIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "quote_id", nullable = false, updatable = false)
    protected QuoteId id;

    @Basic
    @JavaType(UdfQuoteIdJavaType.class)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "udf_quote_id", nullable = false, updatable = false)
    protected UdfQuoteId udfQuoteId;

    @Column(name = "currency", nullable = false, updatable = false, length = StringSizeConstraints.MAX_CURRENCY_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Currency currency;

    @Column(name = "amount", nullable = false, updatable = false, precision = 34, scale = 4)
    protected BigDecimal amount;

    @Column(name = "fees", nullable = false, updatable = false, precision = 34, scale = 4)
    protected BigDecimal fees;

    @Column(name = "amount_type", nullable = false, updatable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected AmountType amountType;

    @Column(name = "scenario", nullable = false, updatable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TransactionScenario scenario;

    @Column(name = "sub_scenario", nullable = false, updatable = false, length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String subScenario;

    @Column(name = "initiator", nullable = false, updatable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TransactionInitiator initiator;

    @Column(name = "initiator_type", nullable = false, updatable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TransactionInitiatorType initiatorType;

    @Column(name = "request_expiration", nullable = false, updatable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant requestExpiration;

    @Embedded
    @AttributeOverrides(value = {@AttributeOverride(name = "partyIdType", column = @Column(name = "payer_party_type", nullable = false, length = 16)),
                                 @AttributeOverride(name = "partyId", column = @Column(name = "payer_party_id", nullable = false, length = 48)),
                                 @AttributeOverride(name = "subId", column = @Column(name = "payer_sub_id", length = 48))})
    protected Party payer;

    @Embedded
    @AttributeOverrides(value = {@AttributeOverride(name = "partyIdType", column = @Column(name = "payee_party_type", nullable = false, length = 16)),
                                 @AttributeOverride(name = "partyId", column = @Column(name = "payee_party_id", nullable = false, length = 48)),
                                 @AttributeOverride(name = "subId", column = @Column(name = "payee_sub_id", length = 48))})

    protected Party payee;

    @Column(name = "response_expiration")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant responseExpiration;

    @Column(name = "transfer_amount", precision = 34, scale = 4)
    protected BigDecimal transferAmount;

    @Column(name = "payee_fsp_fee", precision = 34, scale = 4)
    protected BigDecimal payeeFspFee;

    @Column(name = "payee_fsp_commission", precision = 34, scale = 4)
    protected BigDecimal payeeFspCommission;

    @Column(name = "payee_receive_amount", precision = 34, scale = 4)
    protected BigDecimal payeeReceiveAmount;

    @Column(name = "requested_at", nullable = false, updatable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant requestedAt;

    @Column(name = "responded_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant respondedAt;

    @Column(name = "stage", length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected QuotingStage stage;

    @Column(name = "error", length = StringSizeConstraints.MAX_DESCRIPTION_LENGTH)
    protected String error;

    public Quote(UdfQuoteId udfQuoteId,
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

        assert udfQuoteId != null;
        assert currency != null;
        assert amount != null;
        assert fees != null;
        assert amountType != null;
        assert scenario != null;
        assert subScenario != null;
        assert initiator != null;
        assert initiatorType != null;
        assert requestExpiration != null;
        assert payer != null;
        assert payee != null;

        this.id = new QuoteId(Snowflake.get().nextId());
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

        if (this.requestExpiration.isBefore(Instant.now())) {

            throw new ExpirationNotInFutureException();
        }

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

    public void responded(Instant responseExpiration, BigDecimal transferAmount, BigDecimal payeeFspFee, BigDecimal payeeFspCommission, BigDecimal payeeReceiveAmount)
        throws TransferAmountMismatchException, ReceivingAmountMismatchException {

        assert responseExpiration != null;
        assert transferAmount != null;
        assert payeeFspFee != null;
        assert payeeFspCommission != null;
        assert payeeReceiveAmount != null;

        this.responseExpiration = responseExpiration;
        this.transferAmount = transferAmount;
        this.payeeFspFee = payeeFspFee;
        this.payeeFspCommission = payeeFspCommission;
        this.payeeReceiveAmount = payeeReceiveAmount;
        this.respondedAt = Instant.now();

        if (this.amountType == AmountType.SEND) {

            if (!transferAmount.equals(this.amount)) {
                throw new TransferAmountMismatchException(transferAmount, this.amount, this.amountType);
            }

        } else if (this.amountType == AmountType.RECEIVE) {

            if (!payeeReceiveAmount.equals(this.amount)) {
                throw new ReceivingAmountMismatchException(payeeReceiveAmount, this.amount, this.amountType);
            }
        }

    }

}
