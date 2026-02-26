package org.mojave.core.settlement.contract.command.record;

import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.settlement.AmountType;
import org.mojave.common.datatype.enums.settlement.LiquidityDirection;
import org.mojave.common.datatype.enums.settlement.SettlementType;
import org.mojave.common.datatype.identifier.participant.FspGroupId;
import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.identifier.participant.SspId;
import org.mojave.common.datatype.identifier.settlement.SettlementRecordId;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.transfer.TransferId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface InitiateSettlementProcessCommand {

    Output execute(Input input);

    record Input(SettlementType settlementType,
                 FspId payerFspId,
                 FspId payeeFspId,
                 FspGroupId payerFspGroupId,
                 FspGroupId payeeFspGroupId,
                 Currency currency,
                 TransferId transferId,
                 TransactionId transactionId,
                 Instant transactionAt,
                 List<Line> lines) {

        public record Line(Integer lineNo,
                           FspId partyFspId,
                           LiquidityDirection liquidityDirection,
                           AmountType amountType,
                           BigDecimal amount) { }

    }

    record Output(List<SettlementRecordId> settlementRecordIds, SspId settlementProviderId) { }

}
