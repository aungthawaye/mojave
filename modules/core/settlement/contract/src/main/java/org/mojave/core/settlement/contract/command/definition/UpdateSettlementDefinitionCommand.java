package org.mojave.core.settlement.contract.command.definition;

import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.participant.SspId;
import org.mojave.common.datatype.identifier.settlement.FilterGroupId;
import org.mojave.common.datatype.identifier.settlement.SettlementDefinitionId;

import java.time.Instant;

public interface UpdateSettlementDefinitionCommand {

    Output execute(Input input);

    record Input(SettlementDefinitionId settlementDefinitionId,
                 String name,
                 FilterGroupId payerFilterGroupId,
                 FilterGroupId payeeFilterGroupId,
                 Currency currency,
                 Instant startAt,
                 SspId desiredProviderId) { }

    record Output(SettlementDefinitionId settlementDefinitionId) { }

}
