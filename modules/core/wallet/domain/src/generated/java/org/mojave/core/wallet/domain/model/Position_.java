package org.mojave.core.wallet.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.time.Instant;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.wallet.PositionId;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.wallet.domain.model.Position}
 **/
@StaticMetamodel(Position.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Position_ extends JpaEntity_ {

	
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
	 * @see #position
	 **/
	public static final String POSITION = "position";
	
	/**
	 * @see #reserved
	 **/
	public static final String RESERVED = "reserved";
	
	/**
	 * @see #ndc
	 **/
	public static final String NDC = "ndc";
	
	/**
	 * @see #createdAt
	 **/
	public static final String CREATED_AT = "createdAt";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.wallet.domain.model.Position}
	 **/
	public static volatile EntityType<Position> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.Position#id}
	 **/
	public static volatile SingularAttribute<Position, PositionId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.Position#walletOwnerId}
	 **/
	public static volatile SingularAttribute<Position, WalletOwnerId> walletOwnerId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.Position#currency}
	 **/
	public static volatile SingularAttribute<Position, Currency> currency;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.Position#name}
	 **/
	public static volatile SingularAttribute<Position, String> name;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.Position#position}
	 **/
	public static volatile SingularAttribute<Position, BigDecimal> position;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.Position#reserved}
	 **/
	public static volatile SingularAttribute<Position, BigDecimal> reserved;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.Position#ndc}
	 **/
	public static volatile SingularAttribute<Position, BigDecimal> ndc;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.wallet.domain.model.Position#createdAt}
	 **/
	public static volatile SingularAttribute<Position, Instant> createdAt;

}

