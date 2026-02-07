package org.mojave.rail.fspiop.transfer.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.time.Instant;
import org.mojave.common.datatype.enums.transfer.AbortReason;
import org.mojave.common.datatype.enums.transfer.DisputeReason;
import org.mojave.common.datatype.enums.transfer.TransferStatus;
import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.transfer.TransferId;
import org.mojave.common.datatype.identifier.transfer.UdfTransferId;
import org.mojave.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.component.jpa.JpaEntity_;
import org.mojave.scheme.fspiop.core.AmountType;
import org.mojave.scheme.fspiop.core.Currency;
import org.mojave.scheme.fspiop.core.TransactionScenario;

/**
 * Static metamodel for {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer}
 **/
@StaticMetamodel(Transfer.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Transfer_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #transactionId
	 **/
	public static final String TRANSACTION_ID = "transactionId";
	
	/**
	 * @see #transactionAt
	 **/
	public static final String TRANSACTION_AT = "transactionAt";
	
	/**
	 * @see #udfTransferId
	 **/
	public static final String UDF_TRANSFER_ID = "udfTransferId";
	
	/**
	 * @see #payerFspId
	 **/
	public static final String PAYER_FSP_ID = "payerFspId";
	
	/**
	 * @see #payer
	 **/
	public static final String PAYER = "payer";
	
	/**
	 * @see #payeeFspId
	 **/
	public static final String PAYEE_FSP_ID = "payeeFspId";
	
	/**
	 * @see #payee
	 **/
	public static final String PAYEE = "payee";
	
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
	 * @see #transferCurrency
	 **/
	public static final String TRANSFER_CURRENCY = "transferCurrency";
	
	/**
	 * @see #transferAmount
	 **/
	public static final String TRANSFER_AMOUNT = "transferAmount";
	
	/**
	 * @see #payeeFspFeeCurrency
	 **/
	public static final String PAYEE_FSP_FEE_CURRENCY = "payeeFspFeeCurrency";
	
	/**
	 * @see #payeeFspFeeAmount
	 **/
	public static final String PAYEE_FSP_FEE_AMOUNT = "payeeFspFeeAmount";
	
	/**
	 * @see #payeeFspCommissionCurrency
	 **/
	public static final String PAYEE_FSP_COMMISSION_CURRENCY = "payeeFspCommissionCurrency";
	
	/**
	 * @see #payeeFspCommissionAmount
	 **/
	public static final String PAYEE_FSP_COMMISSION_AMOUNT = "payeeFspCommissionAmount";
	
	/**
	 * @see #payeeReceiveCurrency
	 **/
	public static final String PAYEE_RECEIVE_CURRENCY = "payeeReceiveCurrency";
	
	/**
	 * @see #payeeReceiveAmount
	 **/
	public static final String PAYEE_RECEIVE_AMOUNT = "payeeReceiveAmount";
	
	/**
	 * @see #requestExpiration
	 **/
	public static final String REQUEST_EXPIRATION = "requestExpiration";
	
	/**
	 * @see #reservationId
	 **/
	public static final String RESERVATION_ID = "reservationId";
	
	/**
	 * @see #payerCommitId
	 **/
	public static final String PAYER_COMMIT_ID = "payerCommitId";
	
	/**
	 * @see #payeeCommitId
	 **/
	public static final String PAYEE_COMMIT_ID = "payeeCommitId";
	
	/**
	 * @see #rollbackId
	 **/
	public static final String ROLLBACK_ID = "rollbackId";
	
	/**
	 * @see #status
	 **/
	public static final String STATUS = "status";
	
	/**
	 * @see #receivedAt
	 **/
	public static final String RECEIVED_AT = "receivedAt";
	
	/**
	 * @see #reservedAt
	 **/
	public static final String RESERVED_AT = "reservedAt";
	
	/**
	 * @see #committedAt
	 **/
	public static final String COMMITTED_AT = "committedAt";
	
	/**
	 * @see #abortedAt
	 **/
	public static final String ABORTED_AT = "abortedAt";
	
	/**
	 * @see #abortReason
	 **/
	public static final String ABORT_REASON = "abortReason";
	
	/**
	 * @see #dispute
	 **/
	public static final String DISPUTE = "dispute";
	
	/**
	 * @see #disputeAt
	 **/
	public static final String DISPUTE_AT = "disputeAt";
	
	/**
	 * @see #disputeReason
	 **/
	public static final String DISPUTE_REASON = "disputeReason";
	
	/**
	 * @see #disputeResolved
	 **/
	public static final String DISPUTE_RESOLVED = "disputeResolved";
	
	/**
	 * @see #disputeResolvedAt
	 **/
	public static final String DISPUTE_RESOLVED_AT = "disputeResolvedAt";
	
	/**
	 * @see #reservationTimeoutAt
	 **/
	public static final String RESERVATION_TIMEOUT_AT = "reservationTimeoutAt";
	
	/**
	 * @see #payeeCompletedAt
	 **/
	public static final String PAYEE_COMPLETED_AT = "payeeCompletedAt";
	
	/**
	 * @see #extensions
	 **/
	public static final String EXTENSIONS = "extensions";
	
	/**
	 * @see #ilpFulfilment
	 **/
	public static final String ILP_FULFILMENT = "ilpFulfilment";
	
	/**
	 * @see #ilpPacket
	 **/
	public static final String ILP_PACKET = "ilpPacket";

	
	/**
	 * Static metamodel type for {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer}
	 **/
	public static volatile EntityType<Transfer> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#id}
	 **/
	public static volatile SingularAttribute<Transfer, TransferId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#transactionId}
	 **/
	public static volatile SingularAttribute<Transfer, TransactionId> transactionId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#transactionAt}
	 **/
	public static volatile SingularAttribute<Transfer, Instant> transactionAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#udfTransferId}
	 **/
	public static volatile SingularAttribute<Transfer, UdfTransferId> udfTransferId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#payerFspId}
	 **/
	public static volatile SingularAttribute<Transfer, FspId> payerFspId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#payer}
	 **/
	public static volatile SingularAttribute<Transfer, Party> payer;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#payeeFspId}
	 **/
	public static volatile SingularAttribute<Transfer, FspId> payeeFspId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#payee}
	 **/
	public static volatile SingularAttribute<Transfer, Party> payee;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#amountType}
	 **/
	public static volatile SingularAttribute<Transfer, AmountType> amountType;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#scenario}
	 **/
	public static volatile SingularAttribute<Transfer, TransactionScenario> scenario;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#subScenario}
	 **/
	public static volatile SingularAttribute<Transfer, String> subScenario;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#transferCurrency}
	 **/
	public static volatile SingularAttribute<Transfer, Currency> transferCurrency;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#transferAmount}
	 **/
	public static volatile SingularAttribute<Transfer, BigDecimal> transferAmount;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#payeeFspFeeCurrency}
	 **/
	public static volatile SingularAttribute<Transfer, Currency> payeeFspFeeCurrency;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#payeeFspFeeAmount}
	 **/
	public static volatile SingularAttribute<Transfer, BigDecimal> payeeFspFeeAmount;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#payeeFspCommissionCurrency}
	 **/
	public static volatile SingularAttribute<Transfer, Currency> payeeFspCommissionCurrency;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#payeeFspCommissionAmount}
	 **/
	public static volatile SingularAttribute<Transfer, BigDecimal> payeeFspCommissionAmount;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#payeeReceiveCurrency}
	 **/
	public static volatile SingularAttribute<Transfer, Currency> payeeReceiveCurrency;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#payeeReceiveAmount}
	 **/
	public static volatile SingularAttribute<Transfer, BigDecimal> payeeReceiveAmount;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#requestExpiration}
	 **/
	public static volatile SingularAttribute<Transfer, Instant> requestExpiration;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#reservationId}
	 **/
	public static volatile SingularAttribute<Transfer, PositionUpdateId> reservationId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#payerCommitId}
	 **/
	public static volatile SingularAttribute<Transfer, PositionUpdateId> payerCommitId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#payeeCommitId}
	 **/
	public static volatile SingularAttribute<Transfer, PositionUpdateId> payeeCommitId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#rollbackId}
	 **/
	public static volatile SingularAttribute<Transfer, PositionUpdateId> rollbackId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#status}
	 **/
	public static volatile SingularAttribute<Transfer, TransferStatus> status;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#receivedAt}
	 **/
	public static volatile SingularAttribute<Transfer, Instant> receivedAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#reservedAt}
	 **/
	public static volatile SingularAttribute<Transfer, Instant> reservedAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#committedAt}
	 **/
	public static volatile SingularAttribute<Transfer, Instant> committedAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#abortedAt}
	 **/
	public static volatile SingularAttribute<Transfer, Instant> abortedAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#abortReason}
	 **/
	public static volatile SingularAttribute<Transfer, AbortReason> abortReason;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#dispute}
	 **/
	public static volatile SingularAttribute<Transfer, Boolean> dispute;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#disputeAt}
	 **/
	public static volatile SingularAttribute<Transfer, Instant> disputeAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#disputeReason}
	 **/
	public static volatile SingularAttribute<Transfer, DisputeReason> disputeReason;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#disputeResolved}
	 **/
	public static volatile SingularAttribute<Transfer, Boolean> disputeResolved;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#disputeResolvedAt}
	 **/
	public static volatile SingularAttribute<Transfer, Instant> disputeResolvedAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#reservationTimeoutAt}
	 **/
	public static volatile SingularAttribute<Transfer, Instant> reservationTimeoutAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#payeeCompletedAt}
	 **/
	public static volatile SingularAttribute<Transfer, Instant> payeeCompletedAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#extensions}
	 **/
	public static volatile ListAttribute<Transfer, TransferExtension> extensions;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#ilpFulfilment}
	 **/
	public static volatile SingularAttribute<Transfer, String> ilpFulfilment;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Transfer#ilpPacket}
	 **/
	public static volatile SingularAttribute<Transfer, TransferIlpPacket> ilpPacket;

}

