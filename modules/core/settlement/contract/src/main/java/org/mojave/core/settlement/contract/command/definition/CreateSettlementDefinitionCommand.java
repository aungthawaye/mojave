package org.mojave.core.settlement.contract.command.definition;

import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.participant.FspGroupId;
import org.mojave.common.datatype.identifier.participant.SspId;
import org.mojave.common.datatype.identifier.settlement.SettlementDefinitionId;

import java.time.Instant;

public interface CreateSettlementDefinitionCommand {

    Output execute(Input input);

    record Input(String name,
                 FspGroupId payerFspGroupId,
                 FspGroupId payeeFspGroupId,
                 Currency currency,
                 Instant startAt,
                 SspId desiredProviderId) { }

    record Output(SettlementDefinitionId settlementDefinitionId) { }

}
