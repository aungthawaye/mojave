package org.mojave.core.settlement.contract.command.definition;

import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.participant.FspGroupId;
import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.identifier.participant.SspId;

import java.time.Instant;

public interface FindSettlementProviderCommand {

    Output execute(Input input);

    record Input(Currency currency,
                 Instant transactionAt,
                 FspId payerFspId,
                 FspId payeeFspId,
                 FspGroupId payerFspGroupId,
                 FspGroupId payeeFspGroupId) { }

    record Output(SspId settlementProviderId) { }

}
