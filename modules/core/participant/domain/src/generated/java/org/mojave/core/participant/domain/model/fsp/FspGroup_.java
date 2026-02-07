package org.mojave.core.participant.domain.model.fsp;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import org.mojave.common.datatype.identifier.participant.FspGroupId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.participant.domain.model.fsp.FspGroup}
 **/
@StaticMetamodel(FspGroup.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class FspGroup_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #name
	 **/
	public static final String NAME = "name";
	
	/**
	 * @see #fsps
	 **/
	public static final String FSPS = "fsps";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.participant.domain.model.fsp.FspGroup}
	 **/
	public static volatile EntityType<FspGroup> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.fsp.FspGroup#id}
	 **/
	public static volatile SingularAttribute<FspGroup, FspGroupId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.fsp.FspGroup#name}
	 **/
	public static volatile SingularAttribute<FspGroup, String> name;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.participant.domain.model.fsp.FspGroup#fsps}
	 **/
	public static volatile SetAttribute<FspGroup, Fsp> fsps;

}

