package org.mojave.core.wallet.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.time.Instant;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.wallet.PositionAction;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.PositionId;
import org.mojave.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.wallet.domain.model.PositionUpdate}
 **/
@StaticMetamodel(PositionUpdate.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class PositionUpdate_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #positionId
	 **/
	public static final String POSITION_ID = "positionId";
	
	/**
	 * @see #action
	 **/
	public static final String ACTION = "action";
	
	/**
	 * @see #transactionId
	 **/
	public static final String TRANSACTION_ID = "transactionId";
	
	/**
	 * @see #currency
	 **/
	public static final String CURRENCY = "currency";
	
	/**
	 * @see #amount
	 **/
	public static final String AMOUNT = "amount";
	
	/**
	 * @see #oldPosition
	 **/
	public static final String OLD_POSITION = "oldPosition";
	
	/**
	 * @see #newPosition
	 **/
	public static final String NEW_POSITION = "newPosition";
	
	/**
	 * @see #oldReserved
	 **/
	public static final String OLD_RESERVED = "oldReserved";
	
	/**
	 * @see #newReserved
	 **/
	public static final String NEW_RESERVED = "newReserved";
	
	/**
	 * @see #netDebitCap
	 **/
	public static final String NET_DEBIT_CAP = "netDebitCap";
	
	/**
	 * @see #description
	 **/
	public static final String DESCRIPTION = "description";
	
	/**
	 * @see #transactionAt
	 **/
	public static final String TRANSACTION_AT = "transactionAt";
	
	/**
	 * @see #createdAt
	 **/
	public static final String CREATED_AT = "createdAt";
	
	/**
	 * @see #reservationId
	 **/
	public static final String RESERVATION_ID = "reservationId";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.wallet.domain.model.PositionUpdate}
	 **/
	public static volatile EntityType<PositionUpdate> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.PositionUpdate#id}
	 **/
	public static volatile SingularAttribute<PositionUpdate, PositionUpdateId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.PositionUpdate#positionId}
	 **/
	public static volatile SingularAttribute<PositionUpdate, PositionId> positionId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.PositionUpdate#action}
	 **/
	public static volatile SingularAttribute<PositionUpdate, PositionAction> action;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.PositionUpdate#transactionId}
	 **/
	public static volatile SingularAttribute<PositionUpdate, TransactionId> transactionId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.PositionUpdate#currency}
	 **/
	public static volatile SingularAttribute<PositionUpdate, Currency> currency;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.PositionUpdate#amount}
	 **/
	public static volatile SingularAttribute<PositionUpdate, BigDecimal> amount;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.PositionUpdate#oldPosition}
	 **/
	public static volatile SingularAttribute<PositionUpdate, BigDecimal> oldPosition;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.PositionUpdate#newPosition}
	 **/
	public static volatile SingularAttribute<PositionUpdate, BigDecimal> newPosition;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.PositionUpdate#oldReserved}
	 **/
	public static volatile SingularAttribute<PositionUpdate, BigDecimal> oldReserved;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.PositionUpdate#newReserved}
	 **/
	public static volatile SingularAttribute<PositionUpdate, BigDecimal> newReserved;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.PositionUpdate#netDebitCap}
	 **/
	public static volatile SingularAttribute<PositionUpdate, BigDecimal> netDebitCap;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.PositionUpdate#description}
	 **/
	public static volatile SingularAttribute<PositionUpdate, String> description;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.PositionUpdate#transactionAt}
	 **/
	public static volatile SingularAttribute<PositionUpdate, Instant> transactionAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.PositionUpdate#createdAt}
	 **/
	public static volatile SingularAttribute<PositionUpdate, Instant> createdAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.PositionUpdate#reservationId}
	 **/
	public static volatile SingularAttribute<PositionUpdate, PositionUpdateId> reservationId;

}

