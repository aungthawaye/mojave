package org.mojave.rail.fspiop.quoting.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.time.Instant;
import org.mojave.common.datatype.enums.quoting.QuotingStage;
import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.identifier.quoting.QuoteId;
import org.mojave.common.datatype.identifier.quoting.UdfQuoteId;
import org.mojave.component.jpa.JpaEntity_;
import org.mojave.scheme.fspiop.core.AmountType;
import org.mojave.scheme.fspiop.core.Currency;
import org.mojave.scheme.fspiop.core.TransactionInitiator;
import org.mojave.scheme.fspiop.core.TransactionInitiatorType;
import org.mojave.scheme.fspiop.core.TransactionScenario;

/**
 * Static metamodel for {@link org.mojave.rail.fspiop.quoting.domain.model.Quote}
 **/
@StaticMetamodel(Quote.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Quote_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #payerFspId
	 **/
	public static final String PAYER_FSP_ID = "payerFspId";
	
	/**
	 * @see #payeeFspId
	 **/
	public static final String PAYEE_FSP_ID = "payeeFspId";
	
	/**
	 * @see #udfQuoteId
	 **/
	public static final String UDF_QUOTE_ID = "udfQuoteId";
	
	/**
	 * @see #currency
	 **/
	public static final String CURRENCY = "currency";
	
	/**
	 * @see #amount
	 **/
	public static final String AMOUNT = "amount";
	
	/**
	 * @see #fees
	 **/
	public static final String FEES = "fees";
	
	/**
	 * @see #amountType
	 **/
	public static final String AMOUNT_TYPE = "amountType";
	
	/**
	 * @see #scenario
	 **/
	public static final String SCENARIO = "scenario";
	
	/**
	 * @see #subScenario
	 **/
	public static final String SUB_SCENARIO = "subScenario";
	
	/**
	 * @see #initiator
	 **/
	public static final String INITIATOR = "initiator";
	
	/**
	 * @see #initiatorType
	 **/
	public static final String INITIATOR_TYPE = "initiatorType";
	
	/**
	 * @see #requestExpiration
	 **/
	public static final String REQUEST_EXPIRATION = "requestExpiration";
	
	/**
	 * @see #payer
	 **/
	public static final String PAYER = "payer";
	
	/**
	 * @see #payee
	 **/
	public static final String PAYEE = "payee";
	
	/**
	 * @see #responseExpiration
	 **/
	public static final String RESPONSE_EXPIRATION = "responseExpiration";
	
	/**
	 * @see #transferAmount
	 **/
	public static final String TRANSFER_AMOUNT = "transferAmount";
	
	/**
	 * @see #payeeFspFee
	 **/
	public static final String PAYEE_FSP_FEE = "payeeFspFee";
	
	/**
	 * @see #payeeFspCommission
	 **/
	public static final String PAYEE_FSP_COMMISSION = "payeeFspCommission";
	
	/**
	 * @see #payeeReceiveAmount
	 **/
	public static final String PAYEE_RECEIVE_AMOUNT = "payeeReceiveAmount";
	
	/**
	 * @see #requestedAt
	 **/
	public static final String REQUESTED_AT = "requestedAt";
	
	/**
	 * @see #respondedAt
	 **/
	public static final String RESPONDED_AT = "respondedAt";
	
	/**
	 * @see #stage
	 **/
	public static final String STAGE = "stage";
	
	/**
	 * @see #error
	 **/
	public static final String ERROR = "error";
	
	/**
	 * @see #extensions
	 **/
	public static final String EXTENSIONS = "extensions";
	
	/**
	 * @see #ilpPacket
	 **/
	public static final String ILP_PACKET = "ilpPacket";

	
	/**
	 * Static metamodel type for {@link org.mojave.rail.fspiop.quoting.domain.model.Quote}
	 **/
	public static volatile EntityType<Quote> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#id}
	 **/
	public static volatile SingularAttribute<Quote, QuoteId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#payerFspId}
	 **/
	public static volatile SingularAttribute<Quote, FspId> payerFspId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#payeeFspId}
	 **/
	public static volatile SingularAttribute<Quote, FspId> payeeFspId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#udfQuoteId}
	 **/
	public static volatile SingularAttribute<Quote, UdfQuoteId> udfQuoteId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#currency}
	 **/
	public static volatile SingularAttribute<Quote, Currency> currency;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#amount}
	 **/
	public static volatile SingularAttribute<Quote, BigDecimal> amount;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#fees}
	 **/
	public static volatile SingularAttribute<Quote, BigDecimal> fees;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#amountType}
	 **/
	public static volatile SingularAttribute<Quote, AmountType> amountType;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#scenario}
	 **/
	public static volatile SingularAttribute<Quote, TransactionScenario> scenario;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#subScenario}
	 **/
	public static volatile SingularAttribute<Quote, String> subScenario;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#initiator}
	 **/
	public static volatile SingularAttribute<Quote, TransactionInitiator> initiator;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#initiatorType}
	 **/
	public static volatile SingularAttribute<Quote, TransactionInitiatorType> initiatorType;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#requestExpiration}
	 **/
	public static volatile SingularAttribute<Quote, Instant> requestExpiration;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#payer}
	 **/
	public static volatile SingularAttribute<Quote, Party> payer;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#payee}
	 **/
	public static volatile SingularAttribute<Quote, Party> payee;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#responseExpiration}
	 **/
	public static volatile SingularAttribute<Quote, Instant> responseExpiration;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#transferAmount}
	 **/
	public static volatile SingularAttribute<Quote, BigDecimal> transferAmount;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#payeeFspFee}
	 **/
	public static volatile SingularAttribute<Quote, BigDecimal> payeeFspFee;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#payeeFspCommission}
	 **/
	public static volatile SingularAttribute<Quote, BigDecimal> payeeFspCommission;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#payeeReceiveAmount}
	 **/
	public static volatile SingularAttribute<Quote, BigDecimal> payeeReceiveAmount;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#requestedAt}
	 **/
	public static volatile SingularAttribute<Quote, Instant> requestedAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#respondedAt}
	 **/
	public static volatile SingularAttribute<Quote, Instant> respondedAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#stage}
	 **/
	public static volatile SingularAttribute<Quote, QuotingStage> stage;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#error}
	 **/
	public static volatile SingularAttribute<Quote, String> error;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#extensions}
	 **/
	public static volatile ListAttribute<Quote, QuoteExtension> extensions;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.Quote#ilpPacket}
	 **/
	public static volatile SingularAttribute<Quote, QuoteIlpPacket> ilpPacket;

}

