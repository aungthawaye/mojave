package org.mojave.core.accounting.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;
import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.TerminationStatus;
import org.mojave.common.datatype.enums.accounting.AccountType;
import org.mojave.common.datatype.identifier.accounting.AccountId;
import org.mojave.common.datatype.identifier.accounting.AccountOwnerId;
import org.mojave.common.datatype.identifier.accounting.ChartEntryId;
import org.mojave.common.datatype.type.accounting.AccountCode;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.accounting.domain.model.Account}
 **/
@StaticMetamodel(Account.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Account_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #ownerId
	 **/
	public static final String OWNER_ID = "ownerId";
	
	/**
	 * @see #type
	 **/
	public static final String TYPE = "type";
	
	/**
	 * @see #currency
	 **/
	public static final String CURRENCY = "currency";
	
	/**
	 * @see #code
	 **/
	public static final String CODE = "code";
	
	/**
	 * @see #name
	 **/
	public static final String NAME = "name";
	
	/**
	 * @see #description
	 **/
	public static final String DESCRIPTION = "description";
	
	/**
	 * @see #createdAt
	 **/
	public static final String CREATED_AT = "createdAt";
	
	/**
	 * @see #activationStatus
	 **/
	public static final String ACTIVATION_STATUS = "activationStatus";
	
	/**
	 * @see #terminationStatus
	 **/
	public static final String TERMINATION_STATUS = "terminationStatus";
	
	/**
	 * @see #chartEntryId
	 **/
	public static final String CHART_ENTRY_ID = "chartEntryId";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.accounting.domain.model.Account}
	 **/
	public static volatile EntityType<Account> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.Account#id}
	 **/
	public static volatile SingularAttribute<Account, AccountId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.Account#ownerId}
	 **/
	public static volatile SingularAttribute<Account, AccountOwnerId> ownerId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.Account#type}
	 **/
	public static volatile SingularAttribute<Account, AccountType> type;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.Account#currency}
	 **/
	public static volatile SingularAttribute<Account, Currency> currency;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.Account#code}
	 **/
	public static volatile SingularAttribute<Account, AccountCode> code;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.Account#name}
	 **/
	public static volatile SingularAttribute<Account, String> name;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.Account#description}
	 **/
	public static volatile SingularAttribute<Account, String> description;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.Account#createdAt}
	 **/
	public static volatile SingularAttribute<Account, Instant> createdAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.Account#activationStatus}
	 **/
	public static volatile SingularAttribute<Account, ActivationStatus> activationStatus;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.Account#terminationStatus}
	 **/
	public static volatile SingularAttribute<Account, TerminationStatus> terminationStatus;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.Account#chartEntryId}
	 **/
	public static volatile SingularAttribute<Account, ChartEntryId> chartEntryId;

}

