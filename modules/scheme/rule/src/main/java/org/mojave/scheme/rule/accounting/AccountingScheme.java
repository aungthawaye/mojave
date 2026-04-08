package org.mojave.scheme.rule.accounting;

import org.mojave.scheme.rule.accounting.scenario.AccountingScenario;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AccountingScheme {

    private static final Map<AccountingScenario, ScenarioDefinition> DEFINITIONS;

    static {

        DEFINITIONS = Arrays
                          .stream(AccountingScenario.values())
                          .collect(Collectors.toUnmodifiableMap(
                              Function.identity(),
                              scenario -> new ScenarioDefinition(
                                  scenario, scenario.participants(), scenario.amounts())));
    }

    public static ScenarioDefinition get(final AccountingScenario scenario) {

        var data = DEFINITIONS.get(scenario);

        if (data == null) {
            throw new IllegalArgumentException(
                "No accounting scheme definition found for scenario: " + scenario);
        }

        return data;
    }

    public record ScenarioDefinition(AccountingScenario scenario,
                                     Set<String> participants,
                                     Set<String> amounts) {

        public boolean containsAmountName(final String amountName) {

            return this.amounts.contains(amountName);
        }

        public boolean containsParticipant(final String participant) {

            return this.participants.contains(participant);
        }

    }

}
