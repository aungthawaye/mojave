package org.mojave.rail.fspiop.transfer.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EmbeddableType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import org.mojave.scheme.fspiop.core.PartyIdType;

/**
 * Static metamodel for {@link org.mojave.rail.fspiop.transfer.domain.model.Party}
 **/
@StaticMetamodel(Party.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Party_ {

	
	/**
	 * @see #partyIdType
	 **/
	public static final String PARTY_ID_TYPE = "partyIdType";
	
	/**
	 * @see #partyId
	 **/
	public static final String PARTY_ID = "partyId";
	
	/**
	 * @see #subId
	 **/
	public static final String SUB_ID = "subId";

	
	/**
	 * Static metamodel type for {@link org.mojave.rail.fspiop.transfer.domain.model.Party}
	 **/
	public static volatile EmbeddableType<Party> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Party#partyIdType}
	 **/
	public static volatile SingularAttribute<Party, PartyIdType> partyIdType;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Party#partyId}
	 **/
	public static volatile SingularAttribute<Party, String> partyId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.Party#subId}
	 **/
	public static volatile SingularAttribute<Party, String> subId;

}

