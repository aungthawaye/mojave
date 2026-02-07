package org.mojave.core.accounting.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;
import org.mojave.common.datatype.enums.accounting.AccountType;
import org.mojave.common.datatype.enums.accounting.ChartEntryCategory;
import org.mojave.common.datatype.identifier.accounting.ChartEntryId;
import org.mojave.common.datatype.type.accounting.ChartEntryCode;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.accounting.domain.model.ChartEntry}
 **/
@StaticMetamodel(ChartEntry.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class ChartEntry_ extends JpaEntity_ {

	
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
	 * @see #chart
	 **/
	public static final String CHART = "chart";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.accounting.domain.model.ChartEntry}
	 **/
	public static volatile EntityType<ChartEntry> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.ChartEntry#id}
	 **/
	public static volatile SingularAttribute<ChartEntry, ChartEntryId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.ChartEntry#category}
	 **/
	public static volatile SingularAttribute<ChartEntry, ChartEntryCategory> category;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.ChartEntry#code}
	 **/
	public static volatile SingularAttribute<ChartEntry, ChartEntryCode> code;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.ChartEntry#name}
	 **/
	public static volatile SingularAttribute<ChartEntry, String> name;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.ChartEntry#description}
	 **/
	public static volatile SingularAttribute<ChartEntry, String> description;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.ChartEntry#accountType}
	 **/
	public static volatile SingularAttribute<ChartEntry, AccountType> accountType;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.ChartEntry#createdAt}
	 **/
	public static volatile SingularAttribute<ChartEntry, Instant> createdAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.ChartEntry#chart}
	 **/
	public static volatile SingularAttribute<ChartEntry, Chart> chart;

}

