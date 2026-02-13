package org.mojave.core.transaction.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;
import org.mojave.common.datatype.enums.trasaction.StepPhase;
import org.mojave.common.datatype.identifier.transaction.TransactionStepId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.transaction.domain.model.TransactionStep}
 **/
@StaticMetamodel(TransactionStep.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class TransactionStep_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #phase
	 **/
	public static final String PHASE = "phase";
	
	/**
	 * @see #name
	 **/
	public static final String NAME = "name";
	
	/**
	 * @see #context
	 **/
	public static final String CONTEXT = "context";
	
	/**
	 * @see #payload
	 **/
	public static final String PAYLOAD = "payload";
	
	/**
	 * @see #createdAt
	 **/
	public static final String CREATED_AT = "createdAt";
	
	/**
	 * @see #transaction
	 **/
	public static final String TRANSACTION = "transaction";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.transaction.domain.model.TransactionStep}
	 **/
	public static volatile EntityType<TransactionStep> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.transaction.domain.model.TransactionStep#id}
	 **/
	public static volatile SingularAttribute<TransactionStep, TransactionStepId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.transaction.domain.model.TransactionStep#phase}
	 **/
	public static volatile SingularAttribute<TransactionStep, StepPhase> phase;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.transaction.domain.model.TransactionStep#name}
	 **/
	public static volatile SingularAttribute<TransactionStep, String> name;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.transaction.domain.model.TransactionStep#context}
	 **/
	public static volatile SingularAttribute<TransactionStep, String> context;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.transaction.domain.model.TransactionStep#payload}
	 **/
	public static volatile SingularAttribute<TransactionStep, String> payload;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.transaction.domain.model.TransactionStep#createdAt}
	 **/
	public static volatile SingularAttribute<TransactionStep, Instant> createdAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.transaction.domain.model.TransactionStep#transaction}
	 **/
	public static volatile SingularAttribute<TransactionStep, Transaction> transaction;

}

