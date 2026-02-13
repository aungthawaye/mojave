package org.mojave.core.wallet.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.time.Instant;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.wallet.BalanceId;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.wallet.domain.model.Balance}
 **/
@StaticMetamodel(Balance.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Balance_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #walletOwnerId
	 **/
	public static final String WALLET_OWNER_ID = "walletOwnerId";
	
	/**
	 * @see #currency
	 **/
	public static final String CURRENCY = "currency";
	
	/**
	 * @see #name
	 **/
	public static final String NAME = "name";
	
	/**
	 * @see #balance
	 **/
	public static final String BALANCE = "balance";
	
	/**
	 * @see #createdAt
	 **/
	public static final String CREATED_AT = "createdAt";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.wallet.domain.model.Balance}
	 **/
	public static volatile EntityType<Balance> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.Balance#id}
	 **/
	public static volatile SingularAttribute<Balance, BalanceId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.Balance#walletOwnerId}
	 **/
	public static volatile SingularAttribute<Balance, WalletOwnerId> walletOwnerId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.Balance#currency}
	 **/
	public static volatile SingularAttribute<Balance, Currency> currency;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.Balance#name}
	 **/
	public static volatile SingularAttribute<Balance, String> name;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.Balance#balance}
	 **/
	public static volatile SingularAttribute<Balance, BigDecimal> balance;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.Balance#createdAt}
	 **/
	public static volatile SingularAttribute<Balance, Instant> createdAt;

}

