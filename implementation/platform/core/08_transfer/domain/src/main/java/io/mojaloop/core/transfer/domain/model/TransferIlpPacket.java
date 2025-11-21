package io.mojaloop.core.transfer.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.converter.identifier.transfer.TransferIdJavaType;
import io.mojaloop.core.common.datatype.identifier.transfer.TransferId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(name = "tfr_transfer_ilp_packet")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransferIlpPacket extends JpaEntity<TransferId> {

    @Id
    @JavaType(TransferIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "transfer_id", nullable = false, updatable = false)
    protected TransferId id;

    @Column(name = "ilp_packet", length = StringSizeConstraints.MAX_ILP_PACKET_LENGTH)
    protected String ilpPacket;

    @Column(name = "ilp_condition", length = StringSizeConstraints.MAX_ILP_PACKET_CONDITION_LENGTH)
    protected String condition;

    @Column(name = "ilp_fulfilment", length = StringSizeConstraints.MAX_ILP_PACKET_FULFILMENT_LENGTH)
    protected String fulfilment;

    @MapsId
    @OneToOne
    @JoinColumn(name = "transfer_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "transfer_ilp_packet_transfer_FK"))
    protected Transfer transfer;

    public TransferIlpPacket(Transfer transfer, String ilpPacket, String ilpCondition) {

        assert transfer != null;
        assert ilpPacket != null;
        assert ilpCondition != null;

        this.id = transfer.getId();
        this.ilpPacket = ilpPacket;
        this.condition = ilpCondition;

        this.transfer = transfer;
    }

    public void fulfil(String fulfilment) {

        assert fulfilment != null;

        this.fulfilment = fulfilment;
    }

    @Override
    public TransferId getId() {

        return this.id;
    }

}
