package org.mojave.core.settlement.contract.query;

import org.mojave.common.datatype.identifier.settlement.SettlementDefinitionId;
import org.mojave.core.settlement.contract.data.SettlementDefinitionData;

import java.util.List;

public interface SettlementDefinitionQuery {

    SettlementDefinitionData get(SettlementDefinitionId settlementDefinitionId);

    List<SettlementDefinitionData> getAll();

}
