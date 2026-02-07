package org.mojave.core.transaction.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;
import org.mojave.common.datatype.enums.trasaction.TransactionPhase;
import org.mojave.common.datatype.enums.trasaction.TransactionType;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.transaction.domain.model.Transaction}
 **/
@StaticMetamodel(Transaction.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Transaction_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #type
	 **/
	public static final String TYPE = "type";
	
	/**
	 * @see #phase
	 **/
	public static final String PHASE = "phase";
	
	/**
	 * @see #openAt
	 **/
	public static final String OPEN_AT = "openAt";
	
	/**
	 * @see #closeAt
	 **/
	public static final String CLOSE_AT = "closeAt";
	
	/**
	 * @see #error
	 **/
	public static final String ERROR = "error";
	
	/**
	 * @see #success
	 **/
	public static final String SUCCESS = "success";
	
	/**
	 * @see #steps
	 **/
	public static final String STEPS = "steps";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.transaction.domain.model.Transaction}
	 **/
	public static volatile EntityType<Transaction> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.transaction.domain.model.Transaction#id}
	 **/
	public static volatile SingularAttribute<Transaction, TransactionId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.transaction.domain.model.Transaction#type}
	 **/
	public static volatile SingularAttribute<Transaction, TransactionType> type;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.transaction.domain.model.Transaction#phase}
	 **/
	public static volatile SingularAttribute<Transaction, TransactionPhase> phase;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.transaction.domain.model.Transaction#openAt}
	 **/
	public static volatile SingularAttribute<Transaction, Instant> openAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.transaction.domain.model.Transaction#closeAt}
	 **/
	public static volatile SingularAttribute<Transaction, Instant> closeAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.transaction.domain.model.Transaction#error}
	 **/
	public static volatile SingularAttribute<Transaction, String> error;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.transaction.domain.model.Transaction#success}
	 **/
	public static volatile SingularAttribute<Transaction, Boolean> success;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.transaction.domain.model.Transaction#steps}
	 **/
	public static volatile ListAttribute<Transaction, TransactionStep> steps;

}

