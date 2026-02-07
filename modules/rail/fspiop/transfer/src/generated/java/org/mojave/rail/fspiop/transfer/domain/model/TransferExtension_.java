package org.mojave.rail.fspiop.transfer.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import org.mojave.common.datatype.enums.Direction;
import org.mojave.common.datatype.identifier.transfer.TransferExtensionId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.rail.fspiop.transfer.domain.model.TransferExtension}
 **/
@StaticMetamodel(TransferExtension.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class TransferExtension_ extends JpaEntity_ {

	
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
	 * @see #transfer
	 **/
	public static final String TRANSFER = "transfer";

	
	/**
	 * Static metamodel type for {@link org.mojave.rail.fspiop.transfer.domain.model.TransferExtension}
	 **/
	public static volatile EntityType<TransferExtension> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.TransferExtension#id}
	 **/
	public static volatile SingularAttribute<TransferExtension, TransferExtensionId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.TransferExtension#direction}
	 **/
	public static volatile SingularAttribute<TransferExtension, Direction> direction;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.TransferExtension#key}
	 **/
	public static volatile SingularAttribute<TransferExtension, String> key;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.TransferExtension#value}
	 **/
	public static volatile SingularAttribute<TransferExtension, String> value;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.TransferExtension#transfer}
	 **/
	public static volatile SingularAttribute<TransferExtension, Transfer> transfer;

}

