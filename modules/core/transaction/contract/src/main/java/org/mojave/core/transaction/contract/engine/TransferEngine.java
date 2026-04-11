package org.mojave.core.transaction.contract.engine;

import org.mojave.common.datatype.identifier.wallet.WalletId;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.mojave.scheme.rule.scenario.ScenarioType;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public interface TransferEngine<P> {

    void abort();

    void commit();

    Definition getDefinition();

    ScenarioType getScenario();

    void reserve(P request, ReservationParameterResolver<P> resolver)
        throws WalletEngine.PositionLimitExceededException, WalletEngine.NoPositionUpdateException;

    interface ReservationParameterResolver<P> {

        String description(P parameter);

        WalletId destinationWalletId(P parameter);

        Map<String, BigDecimal> otherAmounts(P parameter);

        WalletId soureWalletId(P parameter);

        BigDecimal transferAmount(P parameter);

    }

    record Parameters(Map<String, WalletOwnerId> wallets, Map<String, BigDecimal> amounts) { }

    record Definition(String scenario, Set<String> participants, Set<String> amounts) { }

}
