package org.mojave.core.accounting.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EmbeddableType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;

/**
 * Static metamodel for {@link org.mojave.core.accounting.domain.model.DrCr}
 **/
@StaticMetamodel(DrCr.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class DrCr_ {

	
	/**
	 * @see #debits
	 **/
	public static final String DEBITS = "debits";
	
	/**
	 * @see #credits
	 **/
	public static final String CREDITS = "credits";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.accounting.domain.model.DrCr}
	 **/
	public static volatile EmbeddableType<DrCr> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.DrCr#debits}
	 **/
	public static volatile SingularAttribute<DrCr, BigDecimal> debits;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.DrCr#credits}
	 **/
	public static volatile SingularAttribute<DrCr, BigDecimal> credits;

}

