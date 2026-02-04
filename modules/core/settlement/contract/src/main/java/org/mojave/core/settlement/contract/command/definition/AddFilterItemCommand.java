package org.mojave.core.settlement.contract.command.definition;

import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.identifier.settlement.FilterGroupId;
import org.mojave.common.datatype.identifier.settlement.FilterItemId;

public interface AddFilterItemCommand {

    Output execute(Input input);

    record Input(FilterGroupId filterGroupId, FspId fspId) { }

    record Output(FilterItemId filterItemId) { }

}
