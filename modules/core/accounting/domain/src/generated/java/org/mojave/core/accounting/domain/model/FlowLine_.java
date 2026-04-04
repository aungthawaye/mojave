package org.mojave.core.accounting.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import org.mojave.common.datatype.enums.accounting.PostingChannel;
import org.mojave.common.datatype.enums.accounting.Side;
import org.mojave.common.datatype.identifier.accounting.FlowLineId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.core.accounting.domain.model.FlowLine}
 **/
@StaticMetamodel(FlowLine.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class FlowLine_ extends JpaEntity_ {

	
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
	 * @see #postingChannel
	 **/
	public static final String POSTING_CHANNEL = "postingChannel";
	
	/**
	 * @see #postingChannelId
	 **/
	public static final String POSTING_CHANNEL_ID = "postingChannelId";
	
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
	 * Static metamodel type for {@link org.mojave.core.accounting.domain.model.FlowLine}
	 **/
	public static volatile EntityType<FlowLine> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.FlowLine#id}
	 **/
	public static volatile SingularAttribute<FlowLine, FlowLineId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.FlowLine#participant}
	 **/
	public static volatile SingularAttribute<FlowLine, String> participant;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.FlowLine#amountName}
	 **/
	public static volatile SingularAttribute<FlowLine, String> amountName;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.FlowLine#side}
	 **/
	public static volatile SingularAttribute<FlowLine, Side> side;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.FlowLine#postingChannel}
	 **/
	public static volatile SingularAttribute<FlowLine, PostingChannel> postingChannel;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.FlowLine#postingChannelId}
	 **/
	public static volatile SingularAttribute<FlowLine, Long> postingChannelId;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.FlowLine#description}
	 **/
	public static volatile SingularAttribute<FlowLine, String> description;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.FlowLine#step}
	 **/
	public static volatile SingularAttribute<FlowLine, Integer> step;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.core.accounting.domain.model.FlowLine#definition}
	 **/
	public static volatile SingularAttribute<FlowLine, FlowDefinition> definition;

}

