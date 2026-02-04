package org.mojave.core.settlement.contract.command.definition;

import org.mojave.common.datatype.identifier.settlement.FilterGroupId;

public interface RemoveFilterGroupCommand {

    Output execute(Input input);

    record Input(FilterGroupId filterGroupId) { }

    record Output(FilterGroupId filterGroupId) { }

}
