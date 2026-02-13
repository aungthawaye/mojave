package org.mojave.core.accounting.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;
import org.mojave.common.datatype.identifier.accounting.ChartId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.accounting.domain.model.Chart}
 **/
@StaticMetamodel(Chart.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Chart_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #name
	 **/
	public static final String NAME = "name";
	
	/**
	 * @see #createdAt
	 **/
	public static final String CREATED_AT = "createdAt";
	
	/**
	 * @see #entries
	 **/
	public static final String ENTRIES = "entries";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.accounting.domain.model.Chart}
	 **/
	public static volatile EntityType<Chart> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.Chart#id}
	 **/
	public static volatile SingularAttribute<Chart, ChartId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.Chart#name}
	 **/
	public static volatile SingularAttribute<Chart, String> name;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.Chart#createdAt}
	 **/
	public static volatile SingularAttribute<Chart, Instant> createdAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.Chart#entries}
	 **/
	public static volatile SetAttribute<Chart, ChartEntry> entries;

}

