package org.mojave.core.wallet.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.time.Instant;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.wallet.BalanceAction;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.BalanceId;
import org.mojave.common.datatype.identifier.wallet.BalanceUpdateId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.wallet.domain.model.BalanceUpdate}
 **/
@StaticMetamodel(BalanceUpdate.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class BalanceUpdate_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #balanceId
	 **/
	public static final String BALANCE_ID = "balanceId";
	
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
	 * @see #oldBalance
	 **/
	public static final String OLD_BALANCE = "oldBalance";
	
	/**
	 * @see #newBalance
	 **/
	public static final String NEW_BALANCE = "newBalance";
	
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
	 * @see #withdrawId
	 **/
	public static final String WITHDRAW_ID = "withdrawId";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.wallet.domain.model.BalanceUpdate}
	 **/
	public static volatile EntityType<BalanceUpdate> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.BalanceUpdate#id}
	 **/
	public static volatile SingularAttribute<BalanceUpdate, BalanceUpdateId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.BalanceUpdate#balanceId}
	 **/
	public static volatile SingularAttribute<BalanceUpdate, BalanceId> balanceId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.BalanceUpdate#action}
	 **/
	public static volatile SingularAttribute<BalanceUpdate, BalanceAction> action;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.BalanceUpdate#transactionId}
	 **/
	public static volatile SingularAttribute<BalanceUpdate, TransactionId> transactionId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.BalanceUpdate#currency}
	 **/
	public static volatile SingularAttribute<BalanceUpdate, Currency> currency;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.BalanceUpdate#amount}
	 **/
	public static volatile SingularAttribute<BalanceUpdate, BigDecimal> amount;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.BalanceUpdate#oldBalance}
	 **/
	public static volatile SingularAttribute<BalanceUpdate, BigDecimal> oldBalance;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.BalanceUpdate#newBalance}
	 **/
	public static volatile SingularAttribute<BalanceUpdate, BigDecimal> newBalance;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.BalanceUpdate#description}
	 **/
	public static volatile SingularAttribute<BalanceUpdate, String> description;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.BalanceUpdate#transactionAt}
	 **/
	public static volatile SingularAttribute<BalanceUpdate, Instant> transactionAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.BalanceUpdate#createdAt}
	 **/
	public static volatile SingularAttribute<BalanceUpdate, Instant> createdAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.BalanceUpdate#withdrawId}
	 **/
	public static volatile SingularAttribute<BalanceUpdate, BalanceUpdateId> withdrawId;

}

