package org.mojave.core.accounting.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.TerminationStatus;
import org.mojave.common.datatype.enums.trasaction.TransactionType;
import org.mojave.common.datatype.identifier.accounting.FlowDefinitionId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.accounting.domain.model.FlowDefinition}
 **/
@StaticMetamodel(FlowDefinition.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class FlowDefinition_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #transactionType
	 **/
	public static final String TRANSACTION_TYPE = "transactionType";
	
	/**
	 * @see #currency
	 **/
	public static final String CURRENCY = "currency";
	
	/**
	 * @see #name
	 **/
	public static final String NAME = "name";
	
	/**
	 * @see #description
	 **/
	public static final String DESCRIPTION = "description";
	
	/**
	 * @see #activationStatus
	 **/
	public static final String ACTIVATION_STATUS = "activationStatus";
	
	/**
	 * @see #terminationStatus
	 **/
	public static final String TERMINATION_STATUS = "terminationStatus";
	
	/**
	 * @see #postings
	 **/
	public static final String POSTINGS = "postings";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.accounting.domain.model.FlowDefinition}
	 **/
	public static volatile EntityType<FlowDefinition> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.FlowDefinition#id}
	 **/
	public static volatile SingularAttribute<FlowDefinition, FlowDefinitionId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.FlowDefinition#transactionType}
	 **/
	public static volatile SingularAttribute<FlowDefinition, TransactionType> transactionType;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.FlowDefinition#currency}
	 **/
	public static volatile SingularAttribute<FlowDefinition, Currency> currency;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.FlowDefinition#name}
	 **/
	public static volatile SingularAttribute<FlowDefinition, String> name;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.FlowDefinition#description}
	 **/
	public static volatile SingularAttribute<FlowDefinition, String> description;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.FlowDefinition#activationStatus}
	 **/
	public static volatile SingularAttribute<FlowDefinition, ActivationStatus> activationStatus;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.FlowDefinition#terminationStatus}
	 **/
	public static volatile SingularAttribute<FlowDefinition, TerminationStatus> terminationStatus;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.FlowDefinition#postings}
	 **/
	public static volatile ListAttribute<FlowDefinition, PostingDefinition> postings;

}

