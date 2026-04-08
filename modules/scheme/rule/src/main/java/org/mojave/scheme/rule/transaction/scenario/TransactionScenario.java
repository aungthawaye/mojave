package org.mojave.scheme.rule.transaction.scenario;

import org.mojave.scheme.rule.transaction.scenario.dimension.DecreaseNdcFlowDimension;
import org.mojave.scheme.rule.transaction.scenario.dimension.FundInFlowDimension;
import org.mojave.scheme.rule.transaction.scenario.dimension.FundOutFlowDimension;
import org.mojave.scheme.rule.transaction.scenario.dimension.FundTransferFlowDimension;
import org.mojave.scheme.rule.transaction.scenario.dimension.IncreaseNdcFlowDimension;
import org.mojave.scheme.rule.transaction.scenario.dimension.SettleFundFlowDimension;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum TransactionScenario {

    DECREASE_NDC(DecreaseNdcFlowDimension.Wallets.class),

    INCREASE_NDC(IncreaseNdcFlowDimension.Wallets.class),

    FUND_TRANSFER(FundTransferFlowDimension.Wallets.class),

    FUND_IN(FundInFlowDimension.Wallets.class),

    FUND_OUT(FundOutFlowDimension.Wallets.class),

    SETTLE_FUND(SettleFundFlowDimension.Wallets.class);

    private final Set<String> wallets;

    TransactionScenario(final Class<? extends Enum<?>> walletType) {

        this(enumNames(walletType));
    }

    TransactionScenario(final Set<String> wallets) {

        this.wallets = Set.copyOf(wallets);
    }

    private static Set<String> enumNames(final Class<? extends Enum<?>> type) {

        return Arrays
                   .stream(type.getEnumConstants())
                   .map(Enum::name)
                   .collect(Collectors.toUnmodifiableSet());
    }

    public Set<String> wallets() {

        return this.wallets;
    }
}
