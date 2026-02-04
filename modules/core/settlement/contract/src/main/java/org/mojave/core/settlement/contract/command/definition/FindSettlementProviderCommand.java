package org.mojave.core.settlement.contract.command.definition;

import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.identifier.participant.SspId;

public interface FindSettlementProviderCommand {

    Output execute(Input input);

    record Input(Currency currency, FspId payerFspId, FspId payeeFspId) { }

    record Output(SspId settlementProviderId) { }

}
