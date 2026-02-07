package org.mojave.core.wallet.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.time.Instant;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.NdcUpdateId;
import org.mojave.common.datatype.identifier.wallet.PositionId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.wallet.domain.model.NdcUpdate}
 **/
@StaticMetamodel(NdcUpdate.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class NdcUpdate_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #positionId
	 **/
	public static final String POSITION_ID = "positionId";
	
	/**
	 * @see #transactionId
	 **/
	public static final String TRANSACTION_ID = "transactionId";
	
	/**
	 * @see #oldNetDebitCap
	 **/
	public static final String OLD_NET_DEBIT_CAP = "oldNetDebitCap";
	
	/**
	 * @see #newNetDebitCap
	 **/
	public static final String NEW_NET_DEBIT_CAP = "newNetDebitCap";
	
	/**
	 * @see #transactionAt
	 **/
	public static final String TRANSACTION_AT = "transactionAt";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.wallet.domain.model.NdcUpdate}
	 **/
	public static volatile EntityType<NdcUpdate> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.NdcUpdate#id}
	 **/
	public static volatile SingularAttribute<NdcUpdate, NdcUpdateId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.NdcUpdate#positionId}
	 **/
	public static volatile SingularAttribute<NdcUpdate, PositionId> positionId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.NdcUpdate#transactionId}
	 **/
	public static volatile SingularAttribute<NdcUpdate, TransactionId> transactionId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.NdcUpdate#oldNetDebitCap}
	 **/
	public static volatile SingularAttribute<NdcUpdate, BigDecimal> oldNetDebitCap;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.NdcUpdate#newNetDebitCap}
	 **/
	public static volatile SingularAttribute<NdcUpdate, BigDecimal> newNetDebitCap;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.NdcUpdate#transactionAt}
	 **/
	public static volatile SingularAttribute<NdcUpdate, Instant> transactionAt;

}

