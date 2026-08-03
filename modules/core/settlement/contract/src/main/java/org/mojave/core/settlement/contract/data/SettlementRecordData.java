package org.mojave.core.settlement.contract.data;

import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.settlement.LiquidityDirection;
import org.mojave.scheme.rule.enums.settlement.SettlementMethod;
import org.mojave.scheme.rule.enums.settlement.SettlementRecordStatus;
import org.mojave.scheme.rule.identifier.participant.FspId;
import org.mojave.scheme.rule.identifier.participant.SspId;
import org.mojave.scheme.rule.identifier.settlement.SettlementAmountId;
import org.mojave.scheme.rule.identifier.settlement.SettlementDefinitionId;
import org.mojave.scheme.rule.identifier.settlement.SettlementModelId;
import org.mojave.scheme.rule.identifier.settlement.SettlementRecordId;
import org.mojave.scheme.rule.identifier.settlement.SettlementWindowId;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.scenario.ScenarioType;

import java.math.BigDecimal;
import java.util.List;

public record SettlementRecordData(SettlementRecordId settlementRecordId,
                                   TransactionId transactionId,
                                   ScenarioType scenario,
                                   Currency currency,
                                   SettlementDefinitionId settlementModelDefinitionId,
                                   SettlementModelId settlementModelId,
                                   SettlementWindowId settlementWindowId,
                                   SettlementMethod settlementMethod,
                                   SspId sspId,
                                   SettlementRecordStatus status,
                                   List<SettlementAmountData> settlementAmounts) {

    public record SettlementAmountData(SettlementAmountId settlementAmountId,
                                       String participant,
                                       FspId fspId,
                                       String amountName,
                                       BigDecimal amount,
                                       Currency currency,
                                       LiquidityDirection direction) { }

}
