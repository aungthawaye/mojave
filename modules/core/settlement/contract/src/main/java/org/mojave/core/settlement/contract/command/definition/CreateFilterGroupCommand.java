package org.mojave.core.settlement.contract.command.definition;

import org.mojave.common.datatype.identifier.settlement.FilterGroupId;

public interface CreateFilterGroupCommand {

    Output execute(Input input);

    record Input(String name) { }

    record Output(FilterGroupId filterGroupId) { }

}
