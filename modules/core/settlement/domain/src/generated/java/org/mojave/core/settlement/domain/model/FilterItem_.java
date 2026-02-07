package org.mojave.core.settlement.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.identifier.settlement.FilterItemId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.settlement.domain.model.FilterItem}
 **/
@StaticMetamodel(FilterItem.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class FilterItem_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #fspId
	 **/
	public static final String FSP_ID = "fspId";
	
	/**
	 * @see #filterGroup
	 **/
	public static final String FILTER_GROUP = "filterGroup";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.settlement.domain.model.FilterItem}
	 **/
	public static volatile EntityType<FilterItem> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.FilterItem#id}
	 **/
	public static volatile SingularAttribute<FilterItem, FilterItemId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.FilterItem#fspId}
	 **/
	public static volatile SingularAttribute<FilterItem, FspId> fspId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.settlement.domain.model.FilterItem#filterGroup}
	 **/
	public static volatile SingularAttribute<FilterItem, FilterGroup> filterGroup;

}

