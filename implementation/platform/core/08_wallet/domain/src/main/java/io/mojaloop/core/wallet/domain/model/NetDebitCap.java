package io.mojaloop.core.wallet.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.core.common.datatype.converter.identifier.wallet.PositionIdJavaType;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(name = "wlt_net_debit_cap")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NetDebitCap extends JpaEntity<PositionId> {

    @Id
    @JavaType(PositionIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "position_id")
    protected PositionId id;

    @Column(name = "net_debit_cap", precision = 34, scale = 4, nullable = false, updatable = true)
    protected BigDecimal netDebitCap;

    @Override
    public PositionId getId() {

        return id;
    }

}
