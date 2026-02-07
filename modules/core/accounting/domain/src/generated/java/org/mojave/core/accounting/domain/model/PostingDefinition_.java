package org.mojave.core.accounting.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import org.mojave.common.datatype.enums.accounting.ReceiveIn;
import org.mojave.common.datatype.enums.accounting.Side;
import org.mojave.common.datatype.identifier.accounting.PostingDefinitionId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.accounting.domain.model.PostingDefinition}
 **/
@StaticMetamodel(PostingDefinition.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class PostingDefinition_ extends JpaEntity_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #participant
	 **/
	public static final String PARTICIPANT = "participant";
	
	/**
	 * @see #amountName
	 **/
	public static final String AMOUNT_NAME = "amountName";
	
	/**
	 * @see #side
	 **/
	public static final String SIDE = "side";
	
	/**
	 * @see #receiveIn
	 **/
	public static final String RECEIVE_IN = "receiveIn";
	
	/**
	 * @see #receiveInId
	 **/
	public static final String RECEIVE_IN_ID = "receiveInId";
	
	/**
	 * @see #description
	 **/
	public static final String DESCRIPTION = "description";
	
	/**
	 * @see #step
	 **/
	public static final String STEP = "step";
	
	/**
	 * @see #definition
	 **/
	public static final String DEFINITION = "definition";

	
	/**
	 * Static metamodel type for {@link org.mojave.core.accounting.domain.model.PostingDefinition}
	 **/
	public static volatile EntityType<PostingDefinition> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.PostingDefinition#id}
	 **/
	public static volatile SingularAttribute<PostingDefinition, PostingDefinitionId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.PostingDefinition#participant}
	 **/
	public static volatile SingularAttribute<PostingDefinition, String> participant;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.PostingDefinition#amountName}
	 **/
	public static volatile SingularAttribute<PostingDefinition, String> amountName;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.PostingDefinition#side}
	 **/
	public static volatile SingularAttribute<PostingDefinition, Side> side;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.PostingDefinition#receiveIn}
	 **/
	public static volatile SingularAttribute<PostingDefinition, ReceiveIn> receiveIn;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.PostingDefinition#receiveInId}
	 **/
	public static volatile SingularAttribute<PostingDefinition, Long> receiveInId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.PostingDefinition#description}
	 **/
	public static volatile SingularAttribute<PostingDefinition, String> description;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.PostingDefinition#step}
	 **/
	public static volatile SingularAttribute<PostingDefinition, Integer> step;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.PostingDefinition#definition}
	 **/
	public static volatile SingularAttribute<PostingDefinition, FlowDefinition> definition;

}

