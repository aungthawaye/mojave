package org.mojave.scheme.rule.transaction.scenario;

import org.mojave.scheme.rule.transaction.scenario.dimension.P2PTransferDimension;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum TransactionScenario {

    P2P_TRANSFER(P2PTransferDimension.Positions.class);

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
