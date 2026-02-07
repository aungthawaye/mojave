package org.mojave.rail.fspiop.quoting.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import org.mojave.common.datatype.enums.Direction;
import org.mojave.common.datatype.identifier.quoting.QuoteExtensionId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.rail.fspiop.quoting.domain.model.QuoteExtension}
 **/
@StaticMetamodel(QuoteExtension.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class QuoteExtension_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #direction
	 **/
	public static final String DIRECTION = "direction";
	
	/**
	 * @see #key
	 **/
	public static final String KEY = "key";
	
	/**
	 * @see #value
	 **/
	public static final String VALUE = "value";
	
	/**
	 * @see #quote
	 **/
	public static final String QUOTE = "quote";

	
	/**
	 * Static metamodel type for {@link org.mojave.rail.fspiop.quoting.domain.model.QuoteExtension}
	 **/
	public static volatile EntityType<QuoteExtension> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.QuoteExtension#id}
	 **/
	public static volatile SingularAttribute<QuoteExtension, QuoteExtensionId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.QuoteExtension#direction}
	 **/
	public static volatile SingularAttribute<QuoteExtension, Direction> direction;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.QuoteExtension#key}
	 **/
	public static volatile SingularAttribute<QuoteExtension, String> key;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.QuoteExtension#value}
	 **/
	public static volatile SingularAttribute<QuoteExtension, String> value;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.QuoteExtension#quote}
	 **/
	public static volatile SingularAttribute<QuoteExtension, Quote> quote;

}

