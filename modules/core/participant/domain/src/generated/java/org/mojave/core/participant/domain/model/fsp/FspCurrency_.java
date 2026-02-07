package org.mojave.core.participant.domain.model.fsp;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;
import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.participant.FspCurrencyId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.participant.domain.model.fsp.FspCurrency}
 **/
@StaticMetamodel(FspCurrency.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class FspCurrency_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #currency
	 **/
	public static final String CURRENCY = "currency";
	
	/**
	 * @see #activationStatus
	 **/
	public static final String ACTIVATION_STATUS = "activationStatus";
	
	/**
	 * @see #createdAt
	 **/
	public static final String CREATED_AT = "createdAt";
	
	/**
	 * @see #fsp
	 **/
	public static final String FSP = "fsp";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.participant.domain.model.fsp.FspCurrency}
	 **/
	public static volatile EntityType<FspCurrency> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.fsp.FspCurrency#id}
	 **/
	public static volatile SingularAttribute<FspCurrency, FspCurrencyId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.fsp.FspCurrency#currency}
	 **/
	public static volatile SingularAttribute<FspCurrency, Currency> currency;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.fsp.FspCurrency#activationStatus}
	 **/
	public static volatile SingularAttribute<FspCurrency, ActivationStatus> activationStatus;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.fsp.FspCurrency#createdAt}
	 **/
	public static volatile SingularAttribute<FspCurrency, Instant> createdAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.fsp.FspCurrency#fsp}
	 **/
	public static volatile SingularAttribute<FspCurrency, Fsp> fsp;

}

