package org.mojave.rail.fspiop.quoting.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import org.mojave.common.datatype.identifier.quoting.QuoteId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.rail.fspiop.quoting.domain.model.QuoteIlpPacket}
 **/
@StaticMetamodel(QuoteIlpPacket.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class QuoteIlpPacket_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #ilpPacket
	 **/
	public static final String ILP_PACKET = "ilpPacket";
	
	/**
	 * @see #condition
	 **/
	public static final String CONDITION = "condition";
	
	/**
	 * @see #quote
	 **/
	public static final String QUOTE = "quote";

	
	/**
	 * Static metamodel type for {@link org.mojave.rail.fspiop.quoting.domain.model.QuoteIlpPacket}
	 **/
	public static volatile EntityType<QuoteIlpPacket> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.QuoteIlpPacket#id}
	 **/
	public static volatile SingularAttribute<QuoteIlpPacket, QuoteId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.QuoteIlpPacket#ilpPacket}
	 **/
	public static volatile SingularAttribute<QuoteIlpPacket, String> ilpPacket;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.QuoteIlpPacket#condition}
	 **/
	public static volatile SingularAttribute<QuoteIlpPacket, String> condition;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.quoting.domain.model.QuoteIlpPacket#quote}
	 **/
	public static volatile SingularAttribute<QuoteIlpPacket, Quote> quote;

}

