package org.mojave.core.participant.domain.model.ssp;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;
import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.TerminationStatus;
import org.mojave.common.datatype.identifier.participant.SspId;
import org.mojave.common.datatype.type.participant.SspCode;
import org.mojave.component.jpa.JpaEntity_;
import org.mojave.core.participant.domain.model.hub.Hub;

/**
 * Static metamodel for {@link org.mojave.core.participant.domain.model.ssp.Ssp}
 **/
@StaticMetamodel(Ssp.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Ssp_ extends JpaEntity_ {

	
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
	 * @see #baseUrl
	 **/
	public static final String BASE_URL = "baseUrl";
	
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
	 * @see #hub
	 **/
	public static final String HUB = "hub";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.participant.domain.model.ssp.Ssp}
	 **/
	public static volatile EntityType<Ssp> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.ssp.Ssp#id}
	 **/
	public static volatile SingularAttribute<Ssp, SspId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.ssp.Ssp#code}
	 **/
	public static volatile SingularAttribute<Ssp, SspCode> code;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.ssp.Ssp#name}
	 **/
	public static volatile SingularAttribute<Ssp, String> name;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.ssp.Ssp#baseUrl}
	 **/
	public static volatile SingularAttribute<Ssp, String> baseUrl;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.ssp.Ssp#activationStatus}
	 **/
	public static volatile SingularAttribute<Ssp, ActivationStatus> activationStatus;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.ssp.Ssp#terminationStatus}
	 **/
	public static volatile SingularAttribute<Ssp, TerminationStatus> terminationStatus;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.ssp.Ssp#createdAt}
	 **/
	public static volatile SingularAttribute<Ssp, Instant> createdAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.ssp.Ssp#currencies}
	 **/
	public static volatile SetAttribute<Ssp, SspCurrency> currencies;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.ssp.Ssp#hub}
	 **/
	public static volatile SingularAttribute<Ssp, Hub> hub;

}

