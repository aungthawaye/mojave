package org.mojave.scheme.rule.accounting.scenario;

import org.mojave.scheme.rule.accounting.scenario.dimension.DecreaseNdcFlowDimension;
import org.mojave.scheme.rule.accounting.scenario.dimension.FundInFlowDimension;
import org.mojave.scheme.rule.accounting.scenario.dimension.FundOutFlowDimension;
import org.mojave.scheme.rule.accounting.scenario.dimension.FundTransferFlowDimension;
import org.mojave.scheme.rule.accounting.scenario.dimension.IncreaseNdcFlowDimension;
import org.mojave.scheme.rule.accounting.scenario.dimension.SettleFundFlowDimension;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum AccountingScenario {

    DECREASE_NDC(
        DecreaseNdcFlowDimension.Participants.class, DecreaseNdcFlowDimension.Amounts.class),

    INCREASE_NDC(
        IncreaseNdcFlowDimension.Participants.class, IncreaseNdcFlowDimension.Amounts.class),

    FUND_TRANSFER(
        FundTransferFlowDimension.Participants.class, FundTransferFlowDimension.Amounts.class),

    FUND_IN(FundInFlowDimension.Participants.class, FundInFlowDimension.Amounts.class),

    FUND_OUT(FundOutFlowDimension.Participants.class, FundOutFlowDimension.Amounts.class),

    SETTLE_FUND(SettleFundFlowDimension.Participants.class, SettleFundFlowDimension.Amounts.class);

    private final Set<String> participants;

    private final Set<String> amounts;

    AccountingScenario(final Class<? extends Enum<?>> participantType,
                       final Class<? extends Enum<?>> amountType) {

        this(enumNames(participantType), enumNames(amountType));
    }

    AccountingScenario(final Set<String> participants, final Set<String> amounts) {

        this.participants = Set.copyOf(participants);
        this.amounts = Set.copyOf(amounts);
    }

    private static Set<String> enumNames(final Class<? extends Enum<?>> type) {

        return Arrays
                   .stream(type.getEnumConstants())
                   .map(Enum::name)
                   .collect(Collectors.toUnmodifiableSet());
    }

    public Set<String> amounts() {

        return this.amounts;
    }

    public Set<String> participants() {

        return this.participants;
    }
}
