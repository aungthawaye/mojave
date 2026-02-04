package org.mojave.core.settlement.domain.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.mojave.common.datatype.converter.identifier.participant.FspIdJavaType;
import org.mojave.common.datatype.converter.identifier.settlement.FilterItemIdJavaType;
import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.identifier.settlement.FilterItemId;
import org.mojave.core.settlement.contract.exception.FspAlreadyExistsInGroupException;
import java.util.Objects;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "stm_filter_item",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {
            "filter_group_id",
            "fsp_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FilterItem {

    @Id
    @JavaType(FilterItemIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "filter_item_id")
    protected FilterItemId id;

    @Basic
    @JavaType(FspIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "fsp_id",
        nullable = false)
    protected FspId fspId;

    @ManyToOne
    @JoinColumn(
        name = "filter_group_id",
        nullable = false)
    protected FilterGroup filterGroup;

    public FilterItem(FilterGroup filterGroup, FspId fspId) {

        Objects.requireNonNull(filterGroup);
        Objects.requireNonNull(fspId);

        this.filterGroup = filterGroup;

        if (this.filterGroup.fspExists(fspId)) {
            throw new FspAlreadyExistsInGroupException();
        }

        this.fspId = fspId;
    }

    public boolean matches(FspId fspId) {

        return this.fspId.equals(fspId);
    }

}
