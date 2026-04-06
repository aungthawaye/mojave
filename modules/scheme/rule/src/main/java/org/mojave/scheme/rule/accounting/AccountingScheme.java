package org.mojave.scheme.rule.accounting;

import org.mojave.scheme.rule.accounting.scenario.AccountingScenario;
import org.mojave.scheme.rule.accounting.scenario.dimension.DecreaseNdcFlowDimension;
import org.mojave.scheme.rule.accounting.scenario.dimension.FundInFlowDimension;
import org.mojave.scheme.rule.accounting.scenario.dimension.FundOutFlowDimension;
import org.mojave.scheme.rule.accounting.scenario.dimension.FundTransferFlowDimension;
import org.mojave.scheme.rule.accounting.scenario.dimension.IncreaseNdcFlowDimension;
import org.mojave.scheme.rule.accounting.scenario.dimension.SettleFundFlowDimension;

import java.util.Map;
import java.util.Set;

public class AccountingScheme {

    private static final Map<AccountingScenario, ScenarioDefinition> DEFINITIONS =
        //@@formatter:off
        Map.of(
            AccountingScenario.DECREASE_NDC,
                new ScenarioDefinition(
                    AccountingScenario.DECREASE_NDC,
                    Set.of(
                        DecreaseNdcFlowDimension.Participants.HUB.name(),
                        DecreaseNdcFlowDimension.Participants.DECREASING_FSP.name()
                    ),
                    Set.of(
                        DecreaseNdcFlowDimension.Amounts.DECREASE_AMOUNT.name()
                    )),
            AccountingScenario.INCREASE_NDC,
                new ScenarioDefinition(
                    AccountingScenario.INCREASE_NDC,
                    Set.of(
                        IncreaseNdcFlowDimension.Participants.HUB.name(),
                        IncreaseNdcFlowDimension.Participants.INCREASING_FSP.name()
                    ),
                    Set.of(
                        IncreaseNdcFlowDimension.Amounts.INCREASE_AMOUNT.name()
                    )),
            AccountingScenario.FUND_IN,
                new ScenarioDefinition(
                    AccountingScenario.FUND_IN,
                    Set.of(
                        FundInFlowDimension.Participants.HUB.name(),
                        FundInFlowDimension.Participants.RECEIVING_FSP.name()
                    ),
                    Set.of(
                        FundInFlowDimension.Amounts.LIQUIDITY_AMOUNT.name()
                    )),
            AccountingScenario.FUND_OUT,
                new ScenarioDefinition(
                    AccountingScenario.FUND_OUT,
                    Set.of(
                        FundOutFlowDimension.Participants.HUB.name(),
                        FundOutFlowDimension.Participants.SOURCE_FSP.name()
                    ),
                    Set.of(
                        FundOutFlowDimension.Amounts.LIQUIDITY_AMOUNT.name()
                    )),
            AccountingScenario.FUND_TRANSFER,
                new ScenarioDefinition(
                    AccountingScenario.FUND_TRANSFER,
                    Set.of(
                        FundTransferFlowDimension.Participants.HUB.name(),
                        FundTransferFlowDimension.Participants.PAYEE_FSP.name(),
                        FundTransferFlowDimension.Participants.PAYEE_FSP.name()
                    ),
                    Set.of(
                        FundTransferFlowDimension.Amounts.TRANSFER_AMOUNT.name()
                    )),
            AccountingScenario.SETTLE_FUND,
                new ScenarioDefinition(
                    AccountingScenario.SETTLE_FUND,
                    Set.of(
                        SettleFundFlowDimension.Participants.HUB.name(),
                        SettleFundFlowDimension.Participants.PAYER_FSP.name(),
                        SettleFundFlowDimension.Participants.PAYEE_FSP.name()
                    ),
                    Set.of(
                        SettleFundFlowDimension.Amounts.SETTLEMENT_AMOUNT.name()
                    ))
        );
        //@@formatter:on

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
