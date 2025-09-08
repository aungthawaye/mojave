package io.mojaloop.core.account.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.converter.identifier.account.ChartIdJavaType;
import io.mojaloop.core.common.datatype.enumeration.account.OwnerType;
import io.mojaloop.core.common.datatype.identifier.account.ChartId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(name = "acc_chart")
public class Chart extends JpaEntity<ChartId> {

    @Id
    @JavaType(ChartIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "chart_of_accounts_id", nullable = false, updatable = false)
    protected ChartId chartId;

    @Column(name = "name", nullable = false, length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String name;

    @Column(name = "owner_type", nullable = false)
    protected OwnerType ownerType;

    @Override
    public ChartId getId() {

        return this.chartId;
    }

}
