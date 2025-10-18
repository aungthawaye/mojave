package io.mojaloop.core.quoting.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.converter.identifier.quoting.QuoteIdJavaType;
import io.mojaloop.core.common.datatype.identifier.quoting.QuoteId;
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
@Table(name = "qot_quote_ilp_packet")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuoteIlpPacket extends JpaEntity<QuoteId> {

    @Id
    @JavaType(QuoteIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "quote_id", nullable = false, updatable = false)
    protected QuoteId id;

    @Column(name = "ilp_packet", length = StringSizeConstraints.MAX_ILP_PACKET_LENGTH)
    protected String ilpPacket;

    @Column(name = "ilp_condition", length = 64)
    protected String condition;

    @Column(name = "ilp_fulfilment", length = 64)
    protected String fulfilment;

    @MapsId
    @OneToOne
    @JoinColumn(name = "quote_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "quote_ilp_packet_quote_FK"))
    protected Quote quote;

    public QuoteIlpPacket(Quote quote) {

        this.id = quote.getId();
        this.quote = quote;
    }

    public void fulfilled(String fulfilment) {

        this.fulfilment = fulfilment;
    }

    @Override
    public QuoteId getId() {

        return this.id;
    }

    public void responded(String ilpPacket, String condition) {

        this.ilpPacket = ilpPacket;
        this.condition = condition;
    }

}
