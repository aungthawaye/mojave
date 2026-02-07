package org.mojave.core.settlement.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import org.mojave.common.datatype.identifier.settlement.FilterGroupId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.settlement.domain.model.FilterGroup}
 **/
@StaticMetamodel(FilterGroup.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class FilterGroup_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #name
	 **/
	public static final String NAME = "name";
	
	/**
	 * @see #items
	 **/
	public static final String ITEMS = "items";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.settlement.domain.model.FilterGroup}
	 **/
	public static volatile EntityType<FilterGroup> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.FilterGroup#id}
	 **/
	public static volatile SingularAttribute<FilterGroup, FilterGroupId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.FilterGroup#name}
	 **/
	public static volatile SingularAttribute<FilterGroup, String> name;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.FilterGroup#items}
	 **/
	public static volatile ListAttribute<FilterGroup, FilterItem> items;

}

