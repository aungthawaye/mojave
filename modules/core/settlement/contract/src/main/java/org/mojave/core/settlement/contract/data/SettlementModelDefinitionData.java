package org.mojave.core.settlement.contract.data;

import org.mojave.scheme.rule.enums.ActivationStatus;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.TerminationStatus;
import org.mojave.scheme.rule.enums.settlement.LiquidityDirection;
import org.mojave.scheme.rule.enums.settlement.TransactionPeriod;
import org.mojave.scheme.rule.identifier.participant.FspGroupId;
import org.mojave.scheme.rule.identifier.settlement.SettlementDefinitionId;
import org.mojave.scheme.rule.identifier.settlement.SettlementModelDefinitionLineId;
import org.mojave.scheme.rule.identifier.settlement.SettlementModelId;
import org.mojave.scheme.rule.scenario.ScenarioType;

import java.util.Comparator;
import java.util.List;

public record SettlementModelDefinitionData(SettlementDefinitionId settlementModelDefinitionId,
                                            ScenarioType scenario,
                                            Currency currency,
                                            FspGroupId fspGroupId,
                                            TransactionPeriod transactionPeriod,
                                            String name,
                                            String description,
                                            SettlementModelId settlementModelId,
                                            ActivationStatus activationStatus,
                                            TerminationStatus terminationStatus,
                                            List<SettlementModelDefinitionLineData> settlementModelDefinitionLines) {

    public SettlementModelDefinitionData {

        settlementModelDefinitionLines = settlementModelDefinitionLines
            .stream()
            .sorted(Comparator.comparing(SettlementModelDefinitionLineData::step))
            .toList();
    }

    public record SettlementModelDefinitionLineData(
        SettlementModelDefinitionLineId settlementModelDefinitionLineId,
        Integer step,
        String participant,
        String amountName,
        Currency currency,
        LiquidityDirection direction,
        String description) { }

}
