/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===
 */

package org.mojave.scheme.rule.query;

import org.mojave.scheme.rule.data.TransactionTypeDefinitionData;
import org.mojave.scheme.rule.dimension.FundInDimension;
import org.mojave.scheme.rule.dimension.FundOutDimension;
import org.mojave.scheme.rule.dimension.FundTransferDimension;
import org.mojave.scheme.rule.dimension.SettlementDimension;
import org.mojave.scheme.rule.exception.TransactionTypeDefinitionNotFoundException;
import org.mojave.scheme.rule.type.TransactionType;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SchemeTransactions {

    private static final Map<TransactionType, TransactionTypeDefinitionData> DEFINITIONS =
        //@@formatter:off
        Map.of(
            TransactionType.FUND_IN,
                new TransactionTypeDefinitionData(
                    TransactionType.FUND_IN, Set.of(
                    FundInDimension.Participants.HUB.name(),
                    FundInDimension.Participants.RECEIVING_FSP.name()),
                    Set.of(FundInDimension.Amounts.LIQUIDITY_AMOUNT.name())),
            TransactionType.FUND_OUT,
                new TransactionTypeDefinitionData(
                    TransactionType.FUND_OUT, Set.of(FundOutDimension.Participants.SOURCE_FSP.name()),
                    Set.of(FundOutDimension.Amounts.LIQUIDITY_AMOUNT.name())),
            TransactionType.FUND_TRANSFER,
                new TransactionTypeDefinitionData(
                    TransactionType.FUND_TRANSFER, Set.of(
                    FundTransferDimension.Participants.PAYER_FSP.name(),
                    FundTransferDimension.Participants.PAYEE_FSP.name()), Set.of(
                    FundTransferDimension.Amounts.TRANSFER_AMOUNT.name(),
                    FundTransferDimension.Amounts.PAYEE_FSP_FEE.name(),
                    FundTransferDimension.Amounts.PAYEE_FSP_COMMISSION.name())),
            TransactionType.SETTLE_FUND,
                new TransactionTypeDefinitionData(
                    TransactionType.SETTLE_FUND, Set.of(
                    SettlementDimension.Participants.HUB.name(),
                    SettlementDimension.Participants.PAYER_FSP.name(),
                    SettlementDimension.Participants.PAYEE_FSP.name()), Set.of(
                    SettlementDimension.Amounts.SETTLEMENT_AMOUNT.name(),
                    SettlementDimension.Amounts.PAYEE_FSP_FEE.name(),
                    SettlementDimension.Amounts.PAYEE_FSP_COMMISSION.name(),
                    SettlementDimension.Amounts.HUB_FEE.name()))
        );
    //@@formatter:on

    public static TransactionTypeDefinitionData get(final TransactionType transactionType)
        throws TransactionTypeDefinitionNotFoundException {

        final var definition = DEFINITIONS.get(transactionType);

        if (definition == null) {
            throw new TransactionTypeDefinitionNotFoundException(transactionType);
        }

        return definition;
    }

    public static List<TransactionTypeDefinitionData> getAll() {

        return DEFINITIONS
                   .values()
                   .stream()
                   .sorted(Comparator.comparing(data -> data.transactionType().name()))
                   .toList();
    }

}
