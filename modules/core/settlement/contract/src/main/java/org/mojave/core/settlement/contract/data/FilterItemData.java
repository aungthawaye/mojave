package org.mojave.core.settlement.contract.data;

import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.identifier.settlement.FilterGroupId;
import org.mojave.common.datatype.identifier.settlement.FilterItemId;

import java.util.Objects;

public record FilterItemData(FilterItemId filterItemId, FspId fspId, FilterGroupId filterGroupId) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof FilterItemData that)) {
            return false;
        }

        return Objects.equals(filterItemId, that.filterItemId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(filterItemId);
    }

}
