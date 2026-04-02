package org.mojave.core.accounting.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;
import org.mojave.common.datatype.enums.accounting.AccountType;
import org.mojave.common.datatype.enums.accounting.ChartEntryCategory;
import org.mojave.common.datatype.identifier.accounting.CoaEntryId;
import org.mojave.common.datatype.type.accounting.CoaEntryCode;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.accounting.domain.model.CoaEntry}
 **/
@StaticMetamodel(CoaEntry.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class CoaEntry_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #category
	 **/
	public static final String CATEGORY = "category";
	
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
	 * @see #accountType
	 **/
	public static final String ACCOUNT_TYPE = "accountType";
	
	/**
	 * @see #createdAt
	 **/
	public static final String CREATED_AT = "createdAt";
	
	/**
	 * @see #coa
	 **/
	public static final String COA = "coa";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.accounting.domain.model.CoaEntry}
	 **/
	public static volatile EntityType<CoaEntry> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.CoaEntry#id}
	 **/
	public static volatile SingularAttribute<CoaEntry, CoaEntryId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.CoaEntry#category}
	 **/
	public static volatile SingularAttribute<CoaEntry, ChartEntryCategory> category;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.CoaEntry#code}
	 **/
	public static volatile SingularAttribute<CoaEntry, CoaEntryCode> code;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.CoaEntry#name}
	 **/
	public static volatile SingularAttribute<CoaEntry, String> name;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.CoaEntry#description}
	 **/
	public static volatile SingularAttribute<CoaEntry, String> description;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.CoaEntry#accountType}
	 **/
	public static volatile SingularAttribute<CoaEntry, AccountType> accountType;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.CoaEntry#createdAt}
	 **/
	public static volatile SingularAttribute<CoaEntry, Instant> createdAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.CoaEntry#coa}
	 **/
	public static volatile SingularAttribute<CoaEntry, Coa> coa;

}

