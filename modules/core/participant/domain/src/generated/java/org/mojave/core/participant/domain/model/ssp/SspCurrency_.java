package org.mojave.core.participant.domain.model.ssp;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;
import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.participant.SspCurrencyId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.participant.domain.model.ssp.SspCurrency}
 **/
@StaticMetamodel(SspCurrency.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class SspCurrency_ extends JpaEntity_ {

	
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
	 * @see #ssp
	 **/
	public static final String SSP = "ssp";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.participant.domain.model.ssp.SspCurrency}
	 **/
	public static volatile EntityType<SspCurrency> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.ssp.SspCurrency#id}
	 **/
	public static volatile SingularAttribute<SspCurrency, SspCurrencyId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.ssp.SspCurrency#currency}
	 **/
	public static volatile SingularAttribute<SspCurrency, Currency> currency;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.ssp.SspCurrency#activationStatus}
	 **/
	public static volatile SingularAttribute<SspCurrency, ActivationStatus> activationStatus;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.ssp.SspCurrency#createdAt}
	 **/
	public static volatile SingularAttribute<SspCurrency, Instant> createdAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.ssp.SspCurrency#ssp}
	 **/
	public static volatile SingularAttribute<SspCurrency, Ssp> ssp;

}

