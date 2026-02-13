package org.mojave.component.jpa;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.MappedSuperclassType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

/**
 * Static metamodel for {@link org.mojave.component.jpa.JpaEntity}
 **/
@StaticMetamodel(JpaEntity.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class JpaEntity_ {

	
	/**
	 * @see #recCreatedAt
	 **/
	public static final String REC_CREATED_AT = "recCreatedAt";
	
	/**
	 * @see #recUpdatedAt
	 **/
	public static final String REC_UPDATED_AT = "recUpdatedAt";
	
	/**
	 * @see #recVersion
	 **/
	public static final String REC_VERSION = "recVersion";

	
	/**
	 * Static metamodel type for {@link org.mojave.component.jpa.JpaEntity}
	 **/
	public static volatile MappedSuperclassType<JpaEntity> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.component.jpa.JpaEntity#recCreatedAt}
	 **/
	public static volatile SingularAttribute<JpaEntity, Instant> recCreatedAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.component.jpa.JpaEntity#recUpdatedAt}
	 **/
	public static volatile SingularAttribute<JpaEntity, Instant> recUpdatedAt;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.component.jpa.JpaEntity#recVersion}
	 **/
	public static volatile SingularAttribute<JpaEntity, Integer> recVersion;

}

