package org.mojave.core.settlement.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.common.datatype.converter.identifier.settlement.FilterGroupIdJavaType;
import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.identifier.settlement.FilterGroupId;
import org.mojave.common.datatype.identifier.settlement.FilterItemId;

import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "stm_filter_group",
    uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FilterGroup {

    @Id
    @JavaType(FilterGroupIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "filter_group_id")
    protected FilterGroupId id;

    @Column(
        name = "name",
        nullable = false,
        length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String name;

    @OneToMany(
        fetch = FetchType.EAGER,
        orphanRemoval = true,
        cascade = CascadeType.ALL)
    @JoinColumn(
        name = "filter_group_id",
        nullable = false)
    protected List<FilterItem> items = new ArrayList<>();

    public FilterItem addItem(FspId fspId) {

        var item = new FilterItem(this, fspId);

        this.items.add(item);

        return item;
    }

    public boolean fspExists(FspId fspId) {

        return this.items.stream().anyMatch(item -> item.matches(fspId));
    }

    public boolean removeItem(FilterItemId filterItemId) {

        if (filterItemId == null || this.items == null || this.items.isEmpty()) {
            return false;
        }

        return this.items.removeIf(item -> filterItemId.equals(item.getId()));
    }

}
