package org.mojave.core.settlement.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;
import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.participant.SspId;
import org.mojave.common.datatype.identifier.settlement.SettlementDefinitionId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.settlement.domain.model.SettlementDefinition}
 **/
@StaticMetamodel(SettlementDefinition.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class SettlementDefinition_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #name
	 **/
	public static final String NAME = "name";
	
	/**
	 * @see #payerFilterGroup
	 **/
	public static final String PAYER_FILTER_GROUP = "payerFilterGroup";
	
	/**
	 * @see #payeeFilterGroup
	 **/
	public static final String PAYEE_FILTER_GROUP = "payeeFilterGroup";
	
	/**
	 * @see #currency
	 **/
	public static final String CURRENCY = "currency";
	
	/**
	 * @see #startAt
	 **/
	public static final String START_AT = "startAt";
	
	/**
	 * @see #desiredProviderId
	 **/
	public static final String DESIRED_PROVIDER_ID = "desiredProviderId";
	
	/**
	 * @see #activationStatus
	 **/
	public static final String ACTIVATION_STATUS = "activationStatus";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.settlement.domain.model.SettlementDefinition}
	 **/
	public static volatile EntityType<SettlementDefinition> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementDefinition#id}
	 **/
	public static volatile SingularAttribute<SettlementDefinition, SettlementDefinitionId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementDefinition#name}
	 **/
	public static volatile SingularAttribute<SettlementDefinition, String> name;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementDefinition#payerFilterGroup}
	 **/
	public static volatile SingularAttribute<SettlementDefinition, FilterGroup> payerFilterGroup;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementDefinition#payeeFilterGroup}
	 **/
	public static volatile SingularAttribute<SettlementDefinition, FilterGroup> payeeFilterGroup;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementDefinition#currency}
	 **/
	public static volatile SingularAttribute<SettlementDefinition, Currency> currency;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementDefinition#startAt}
	 **/
	public static volatile SingularAttribute<SettlementDefinition, Instant> startAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementDefinition#desiredProviderId}
	 **/
	public static volatile SingularAttribute<SettlementDefinition, SspId> desiredProviderId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.SettlementDefinition#activationStatus}
	 **/
	public static volatile SingularAttribute<SettlementDefinition, ActivationStatus> activationStatus;

}

