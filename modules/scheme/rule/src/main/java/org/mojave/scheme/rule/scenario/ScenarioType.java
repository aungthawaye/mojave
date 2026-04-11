package org.mojave.scheme.rule.scenario;

import org.mojave.scheme.rule.scenario.dimension.DecreaseNdcDimension;
import org.mojave.scheme.rule.scenario.dimension.FundInDimension;
import org.mojave.scheme.rule.scenario.dimension.FundOutDimension;
import org.mojave.scheme.rule.scenario.dimension.P2PTransferDimension;
import org.mojave.scheme.rule.scenario.dimension.IncreaseNdcDimension;
import org.mojave.scheme.rule.scenario.dimension.SettleFundDimension;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum ScenarioType {

    DECREASE_NDC(
        DecreaseNdcDimension.Participants.class, DecreaseNdcDimension.Amounts.class),

    INCREASE_NDC(
        IncreaseNdcDimension.Participants.class, IncreaseNdcDimension.Amounts.class),

    P2P_TRANSFER(
        P2PTransferDimension.Participants.class, P2PTransferDimension.Amounts.class),

    FUND_IN(FundInDimension.Participants.class, FundInDimension.Amounts.class),

    FUND_OUT(FundOutDimension.Participants.class, FundOutDimension.Amounts.class),

    SETTLE_FUND(SettleFundDimension.Participants.class, SettleFundDimension.Amounts.class);

    private final Set<String> participants;

    private final Set<String> amounts;

    ScenarioType(final Class<? extends Enum<?>> participantType,
                 final Class<? extends Enum<?>> amountType) {

        this(enumNames(participantType), enumNames(amountType));
    }

    ScenarioType(final Set<String> participants, final Set<String> amounts) {

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
