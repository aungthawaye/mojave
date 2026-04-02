package org.mojave.core.accounting.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;
import org.mojave.common.datatype.identifier.accounting.CoaId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.accounting.domain.model.Coa}
 **/
@StaticMetamodel(Coa.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Coa_ extends JpaEntity_ {

	
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
	 * Static metamodel type for {@link org.mojave.core.accounting.domain.model.Coa}
	 **/
	public static volatile EntityType<Coa> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.Coa#id}
	 **/
	public static volatile SingularAttribute<Coa, CoaId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.Coa#name}
	 **/
	public static volatile SingularAttribute<Coa, String> name;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.Coa#createdAt}
	 **/
	public static volatile SingularAttribute<Coa, Instant> createdAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.Coa#entries}
	 **/
	public static volatile SetAttribute<Coa, CoaEntry> entries;

}

