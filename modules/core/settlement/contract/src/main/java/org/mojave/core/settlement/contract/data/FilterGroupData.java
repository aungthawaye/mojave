package org.mojave.core.settlement.contract.data;

import org.mojave.common.datatype.identifier.settlement.FilterGroupId;

import java.util.List;
import java.util.Objects;

public record FilterGroupData(FilterGroupId filterGroupId,
                              String name,
                              List<FilterItemData> items) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof FilterGroupData that)) {
            return false;
        }

        return Objects.equals(filterGroupId, that.filterGroupId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(filterGroupId);
    }

}
