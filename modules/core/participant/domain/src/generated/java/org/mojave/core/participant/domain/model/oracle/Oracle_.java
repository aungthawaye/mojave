package org.mojave.core.participant.domain.model.oracle;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;
import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.TerminationStatus;
import org.mojave.common.datatype.enums.participant.PartyIdType;
import org.mojave.common.datatype.identifier.participant.OracleId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.participant.domain.model.oracle.Oracle}
 **/
@StaticMetamodel(Oracle.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Oracle_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #type
	 **/
	public static final String TYPE = "type";
	
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
	 * Static metamodel type for {@link org.mojave.core.participant.domain.model.oracle.Oracle}
	 **/
	public static volatile EntityType<Oracle> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.oracle.Oracle#id}
	 **/
	public static volatile SingularAttribute<Oracle, OracleId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.oracle.Oracle#type}
	 **/
	public static volatile SingularAttribute<Oracle, PartyIdType> type;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.oracle.Oracle#name}
	 **/
	public static volatile SingularAttribute<Oracle, String> name;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.oracle.Oracle#baseUrl}
	 **/
	public static volatile SingularAttribute<Oracle, String> baseUrl;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.oracle.Oracle#activationStatus}
	 **/
	public static volatile SingularAttribute<Oracle, ActivationStatus> activationStatus;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.oracle.Oracle#terminationStatus}
	 **/
	public static volatile SingularAttribute<Oracle, TerminationStatus> terminationStatus;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.oracle.Oracle#createdAt}
	 **/
	public static volatile SingularAttribute<Oracle, Instant> createdAt;

}

