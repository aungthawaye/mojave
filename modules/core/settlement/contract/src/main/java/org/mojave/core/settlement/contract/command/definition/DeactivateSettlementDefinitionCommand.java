package org.mojave.core.settlement.contract.command.definition;

import org.mojave.common.datatype.identifier.settlement.SettlementDefinitionId;

public interface DeactivateSettlementDefinitionCommand {

    Output execute(Input input);

    record Input(SettlementDefinitionId settlementDefinitionId) { }

    record Output(SettlementDefinitionId settlementDefinitionId) { }

}
