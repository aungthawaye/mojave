package org.mojave.core.participant.domain.model.hub;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;
import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.participant.HubCurrencyId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.participant.domain.model.hub.HubCurrency}
 **/
@StaticMetamodel(HubCurrency.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class HubCurrency_ extends JpaEntity_ {

	
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
	 * @see #hub
	 **/
	public static final String HUB = "hub";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.participant.domain.model.hub.HubCurrency}
	 **/
	public static volatile EntityType<HubCurrency> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.hub.HubCurrency#id}
	 **/
	public static volatile SingularAttribute<HubCurrency, HubCurrencyId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.hub.HubCurrency#currency}
	 **/
	public static volatile SingularAttribute<HubCurrency, Currency> currency;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.hub.HubCurrency#activationStatus}
	 **/
	public static volatile SingularAttribute<HubCurrency, ActivationStatus> activationStatus;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.hub.HubCurrency#createdAt}
	 **/
	public static volatile SingularAttribute<HubCurrency, Instant> createdAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.hub.HubCurrency#hub}
	 **/
	public static volatile SingularAttribute<HubCurrency, Hub> hub;

}

