package org.mojave.rail.fspiop.transfer.domain.model;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import org.mojave.common.datatype.identifier.transfer.TransferId;
import org.mojave.component.jpa.JpaEntity_;

/**
 * Static metamodel for {@link org.mojave.rail.fspiop.transfer.domain.model.TransferIlpPacket}
 **/
@StaticMetamodel(TransferIlpPacket.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class TransferIlpPacket_ extends JpaEntity_ {

	
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
	 * @see #transfer
	 **/
	public static final String TRANSFER = "transfer";

	
	/**
	 * Static metamodel type for {@link org.mojave.rail.fspiop.transfer.domain.model.TransferIlpPacket}
	 **/
	public static volatile EntityType<TransferIlpPacket> class_;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.TransferIlpPacket#id}
	 **/
	public static volatile SingularAttribute<TransferIlpPacket, TransferId> id;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.TransferIlpPacket#ilpPacket}
	 **/
	public static volatile SingularAttribute<TransferIlpPacket, String> ilpPacket;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.TransferIlpPacket#condition}
	 **/
	public static volatile SingularAttribute<TransferIlpPacket, String> condition;
	
	/**
	 * Static metamodel for attribute {@link org.mojave.rail.fspiop.transfer.domain.model.TransferIlpPacket#transfer}
	 **/
	public static volatile SingularAttribute<TransferIlpPacket, Transfer> transfer;

}

