package org.mojave.core.settlement.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.time.Instant;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.settlement.AmountType;
import org.mojave.common.datatype.enums.settlement.LiquidityDirection;
import org.mojave.common.datatype.enums.settlement.SettlementType;
import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.identifier.participant.SspId;
import org.mojave.common.datatype.identifier.settlement.SettlementBatchId;
import org.mojave.common.datatype.identifier.settlement.SettlementId;
import org.mojave.common.datatype.identifier.settlement.SettlementRecordId;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.transfer.TransferId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.settlement.domain.model.SettlementRecord}
 **/
@StaticMetamodel(SettlementRecord.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class SettlementRecord_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #sspId
	 **/
	public static final String SSP_ID = "sspId";
	
	/**
	 * @see #type
	 **/
	public static final String TYPE = "type";
	
	/**
	 * @see #settlementId
	 **/
	public static final String SETTLEMENT_ID = "settlementId";
	
	/**
	 * @see #settlementBatchId
	 **/
	public static final String SETTLEMENT_BATCH_ID = "settlementBatchId";
	
	/**
	 * @see #payerFspId
	 **/
	public static final String PAYER_FSP_ID = "payerFspId";
	
	/**
	 * @see #payeeFspId
	 **/
	public static final String PAYEE_FSP_ID = "payeeFspId";
	
	/**
	 * @see #partyFspId
	 **/
	public static final String PARTY_FSP_ID = "partyFspId";
	
	/**
	 * @see #liquidityDirection
	 **/
	public static final String LIQUIDITY_DIRECTION = "liquidityDirection";
	
	/**
	 * @see #amountType
	 **/
	public static final String AMOUNT_TYPE = "amountType";
	
	/**
	 * @see #lineNo
	 **/
	public static final String LINE_NO = "lineNo";
	
	/**
	 * @see #currency
	 **/
	public static final String CURRENCY = "currency";
	
	/**
	 * @see #amount
	 **/
	public static final String AMOUNT = "amount";
	
	/**
	 * @see #transferId
	 **/
	public static final String TRANSFER_ID = "transferId";
	
	/**
	 * @see #transactionId
	 **/
	public static final String TRANSACTION_ID = "transactionId";
	
	/**
	 * @see #transactionAt
	 **/
	public static final String TRANSACTION_AT = "transactionAt";
	
	/**
	 * @see #initiatedAt
	 **/
	public static final String INITIATED_AT = "initiatedAt";
	
	/**
	 * @see #preparedAt
	 **/
	public static final String PREPARED_AT = "preparedAt";
	
	/**
	 * @see #completedAt
	 **/
	public static final String COMPLETED_AT = "completedAt";
	
	/**
	 * @see #error
	 **/
	public static final String ERROR = "error";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.settlement.domain.model.SettlementRecord}
	 **/
	public static volatile EntityType<SettlementRecord> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementRecord#id}
	 **/
	public static volatile SingularAttribute<SettlementRecord, SettlementRecordId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementRecord#sspId}
	 **/
	public static volatile SingularAttribute<SettlementRecord, SspId> sspId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementRecord#type}
	 **/
	public static volatile SingularAttribute<SettlementRecord, SettlementType> type;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementRecord#settlementId}
	 **/
	public static volatile SingularAttribute<SettlementRecord, SettlementId> settlementId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementRecord#settlementBatchId}
	 **/
	public static volatile SingularAttribute<SettlementRecord, SettlementBatchId> settlementBatchId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementRecord#payerFspId}
	 **/
	public static volatile SingularAttribute<SettlementRecord, FspId> payerFspId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementRecord#payeeFspId}
	 **/
	public static volatile SingularAttribute<SettlementRecord, FspId> payeeFspId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementRecord#partyFspId}
	 **/
	public static volatile SingularAttribute<SettlementRecord, FspId> partyFspId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementRecord#liquidityDirection}
	 **/
	public static volatile SingularAttribute<SettlementRecord, LiquidityDirection> liquidityDirection;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementRecord#amountType}
	 **/
	public static volatile SingularAttribute<SettlementRecord, AmountType> amountType;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementRecord#lineNo}
	 **/
	public static volatile SingularAttribute<SettlementRecord, Integer> lineNo;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementRecord#currency}
	 **/
	public static volatile SingularAttribute<SettlementRecord, Currency> currency;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementRecord#amount}
	 **/
	public static volatile SingularAttribute<SettlementRecord, BigDecimal> amount;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementRecord#transferId}
	 **/
	public static volatile SingularAttribute<SettlementRecord, TransferId> transferId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementRecord#transactionId}
	 **/
	public static volatile SingularAttribute<SettlementRecord, TransactionId> transactionId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementRecord#transactionAt}
	 **/
	public static volatile SingularAttribute<SettlementRecord, Instant> transactionAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementRecord#initiatedAt}
	 **/
	public static volatile SingularAttribute<SettlementRecord, Instant> initiatedAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementRecord#preparedAt}
	 **/
	public static volatile SingularAttribute<SettlementRecord, Instant> preparedAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementRecord#completedAt}
	 **/
	public static volatile SingularAttribute<SettlementRecord, Instant> completedAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementRecord#error}
	 **/
	public static volatile SingularAttribute<SettlementRecord, String> error;

}

