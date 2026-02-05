package org.mojave.core.settlement.contract.command.definition;

import org.mojave.common.datatype.identifier.settlement.FilterGroupId;
import org.mojave.common.datatype.identifier.settlement.FilterItemId;

public interface RemoveFilterItemCommand {

    Output execute(Input input);

    record Input(FilterGroupId filterGroupId, FilterItemId filterItemId) { }

    record Output(FilterGroupId filterGroupId) { }

}
