package org.mojave.core.participant.domain.model.hub;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;
import org.mojave.common.datatype.identifier.participant.HubId;
import org.mojave.component.jpa.JpaEntity_;
import org.mojave.core.participant.domain.model.fsp.Fsp;
import org.mojave.core.participant.domain.model.ssp.Ssp;

/**
 * Static metamodel for {@link org.mojave.core.participant.domain.model.hub.Hub}
 **/
@StaticMetamodel(Hub.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Hub_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #name
	 **/
	public static final String NAME = "name";
	
	/**
	 * @see #createdAt
	 **/
	public static final String CREATED_AT = "createdAt";
	
	/**
	 * @see #currencies
	 **/
	public static final String CURRENCIES = "currencies";
	
	/**
	 * @see #fsps
	 **/
	public static final String FSPS = "fsps";
	
	/**
	 * @see #ssps
	 **/
	public static final String SSPS = "ssps";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.participant.domain.model.hub.Hub}
	 **/
	public static volatile EntityType<Hub> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.hub.Hub#id}
	 **/
	public static volatile SingularAttribute<Hub, HubId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.hub.Hub#name}
	 **/
	public static volatile SingularAttribute<Hub, String> name;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.hub.Hub#createdAt}
	 **/
	public static volatile SingularAttribute<Hub, Instant> createdAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.hub.Hub#currencies}
	 **/
	public static volatile SetAttribute<Hub, HubCurrency> currencies;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.hub.Hub#fsps}
	 **/
	public static volatile SetAttribute<Hub, Fsp> fsps;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.hub.Hub#ssps}
	 **/
	public static volatile SetAttribute<Hub, Ssp> ssps;

}

