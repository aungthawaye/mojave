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

package org.mojave.scheme.common.datatype.enums.trasaction;

import lombok.Getter;

import java.util.Collections;
import java.util.Set;

public enum TransactionType {

    FUND_IN(
        new Participants(Set.of(
            FundInDimension.Participants.HUB.name(),
            FundInDimension.Participants.RECEIVING_FSP.name())),
        new Amounts(Set.of(FundInDimension.Amounts.LIQUIDITY_AMOUNT.name()))),

    FUND_OUT(
        new Participants(Set.of(FundOutDimension.Participants.SOURCE_FSP.name())),
        new Amounts(Set.of(FundOutDimension.Amounts.LIQUIDITY_AMOUNT.name()))),

    FUND_TRANSFER(
        new Participants(Set.of(
            FundTransferDimension.Participants.PAYER_FSP.name(),
            FundTransferDimension.Participants.PAYEE_FSP.name())), new Amounts(Set.of(
        FundTransferDimension.Amounts.TRANSFER_AMOUNT.name(),
        FundTransferDimension.Amounts.PAYEE_FSP_FEE.name(),
        FundTransferDimension.Amounts.PAYEE_FSP_COMMISSION.name()))),

    SETTLE_FUND(
        new Participants(Set.of(
            SettlementDimension.Participants.PAYER_FSP.name(),
            SettlementDimension.Participants.PAYEE_FSP.name())), new Amounts(Set.of(
        SettlementDimension.Amounts.SETTLEMENT_AMOUNT.name(),
        SettlementDimension.Amounts.PAYEE_FSP_FEE.name(),
        SettlementDimension.Amounts.PAYEE_FSP_COMMISSION.name(),
        SettlementDimension.Amounts.HUB_FEE.name()))),
    ;

    @Getter
    private final Participants participants;

    @Getter
    private final Amounts amounts;

    TransactionType(Participants participants, Amounts amounts) {

        this.participants = participants;
        this.amounts = amounts;
    }

    public record Amounts(Set<String> names) {

        public Amounts(Set<String> names) {

            this.names = Collections.unmodifiableSet(names);
        }

        public boolean contains(String name) {

            return this.names.contains(name);
        }

    }

    public record Participants(Set<String> types) {

        public Participants(Set<String> types) {

            this.types = Collections.unmodifiableSet(types);
        }

        public boolean contains(String name) {

            return this.types.contains(name);
        }

    }
}
