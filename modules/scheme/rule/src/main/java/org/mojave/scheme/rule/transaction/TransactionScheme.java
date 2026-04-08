package org.mojave.scheme.rule.transaction;

import org.mojave.scheme.rule.transaction.scenario.TransactionScenario;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TransactionScheme {

    private static final Map<TransactionScenario, ScenarioDefinition> DEFINITIONS;

    static {

        DEFINITIONS = Arrays
                          .stream(TransactionScenario.values())
                          .collect(Collectors.toUnmodifiableMap(
                              Function.identity(),
                              scenario -> new ScenarioDefinition(scenario, scenario.wallets())));
    }

    public static ScenarioDefinition get(final TransactionScenario scenario) {

        var data = DEFINITIONS.get(scenario);

        if (data == null) {
            throw new IllegalArgumentException(
                "No transaction scheme definition found for scenario: " + scenario);
        }

        return data;
    }

    public record ScenarioDefinition(TransactionScenario scenario, Set<String> wallets) {

        public boolean containsWallet(final String wallet) {

            return this.wallets.contains(wallet);
        }

    }

}
