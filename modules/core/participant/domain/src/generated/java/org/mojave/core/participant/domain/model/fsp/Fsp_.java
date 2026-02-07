package org.mojave.core.participant.domain.model.fsp;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;
import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.TerminationStatus;
import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.type.participant.FspCode;
import org.mojave.component.jpa.JpaEntity_;
import org.mojave.core.participant.domain.model.hub.Hub;

/**
 * Static metamodel for {@link org.mojave.core.participant.domain.model.fsp.Fsp}
 **/
@StaticMetamodel(Fsp.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Fsp_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #code
	 **/
	public static final String CODE = "code";
	
	/**
	 * @see #name
	 **/
	public static final String NAME = "name";
	
	/**
	 * @see #activationStatus
	 **/
	public static final String ACTIVATION_STATUS = "activationStatus";
	
	/**
	 * @see #terminationStatus
	 **/
	public static final String TERMINATION_STATUS = "terminationStatus";
	
	/**
	 * @see #createdAt
	 **/
	public static final String CREATED_AT = "createdAt";
	
	/**
	 * @see #currencies
	 **/
	public static final String CURRENCIES = "currencies";
	
	/**
	 * @see #endpoints
	 **/
	public static final String ENDPOINTS = "endpoints";
	
	/**
	 * @see #hub
	 **/
	public static final String HUB = "hub";
	
	/**
	 * @see #fspGroup
	 **/
	public static final String FSP_GROUP = "fspGroup";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.participant.domain.model.fsp.Fsp}
	 **/
	public static volatile EntityType<Fsp> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.fsp.Fsp#id}
	 **/
	public static volatile SingularAttribute<Fsp, FspId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.fsp.Fsp#code}
	 **/
	public static volatile SingularAttribute<Fsp, FspCode> code;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.fsp.Fsp#name}
	 **/
	public static volatile SingularAttribute<Fsp, String> name;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.fsp.Fsp#activationStatus}
	 **/
	public static volatile SingularAttribute<Fsp, ActivationStatus> activationStatus;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.fsp.Fsp#terminationStatus}
	 **/
	public static volatile SingularAttribute<Fsp, TerminationStatus> terminationStatus;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.fsp.Fsp#createdAt}
	 **/
	public static volatile SingularAttribute<Fsp, Instant> createdAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.fsp.Fsp#currencies}
	 **/
	public static volatile SetAttribute<Fsp, FspCurrency> currencies;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.fsp.Fsp#endpoints}
	 **/
	public static volatile SetAttribute<Fsp, FspEndpoint> endpoints;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.fsp.Fsp#hub}
	 **/
	public static volatile SingularAttribute<Fsp, Hub> hub;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.fsp.Fsp#fspGroup}
	 **/
	public static volatile SingularAttribute<Fsp, FspGroup> fspGroup;

}

