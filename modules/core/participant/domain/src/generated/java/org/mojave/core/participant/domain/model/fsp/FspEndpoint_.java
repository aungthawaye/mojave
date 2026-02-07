package org.mojave.core.participant.domain.model.fsp;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;
import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.participant.EndpointType;
import org.mojave.common.datatype.identifier.participant.FspEndpointId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.participant.domain.model.fsp.FspEndpoint}
 **/
@StaticMetamodel(FspEndpoint.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class FspEndpoint_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #type
	 **/
	public static final String TYPE = "type";
	
	/**
	 * @see #baseUrl
	 **/
	public static final String BASE_URL = "baseUrl";
	
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
	 * Static metamodel type for {@link org.mojave.core.participant.domain.model.fsp.FspEndpoint}
	 **/
	public static volatile EntityType<FspEndpoint> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.fsp.FspEndpoint#id}
	 **/
	public static volatile SingularAttribute<FspEndpoint, FspEndpointId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.fsp.FspEndpoint#type}
	 **/
	public static volatile SingularAttribute<FspEndpoint, EndpointType> type;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.fsp.FspEndpoint#baseUrl}
	 **/
	public static volatile SingularAttribute<FspEndpoint, String> baseUrl;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.fsp.FspEndpoint#activationStatus}
	 **/
	public static volatile SingularAttribute<FspEndpoint, ActivationStatus> activationStatus;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.fsp.FspEndpoint#createdAt}
	 **/
	public static volatile SingularAttribute<FspEndpoint, Instant> createdAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.fsp.FspEndpoint#fsp}
	 **/
	public static volatile SingularAttribute<FspEndpoint, Fsp> fsp;

}

