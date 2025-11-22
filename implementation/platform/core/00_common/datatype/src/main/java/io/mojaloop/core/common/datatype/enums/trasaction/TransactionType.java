/*-
 * ================================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
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
 * ================================================================================
 */

package io.mojaloop.core.common.datatype.enums.trasaction;

import lombok.Getter;

import java.util.Collections;
import java.util.Set;

public enum TransactionType {

    /**
     * Define the type of Transaction. Each transaction will have its own requirements regarding
     * 1. Who (is/are) involved.
     * 2. What (amounts) are involved?
     */

    FUND_IN(
        new Participants(Set.of(FundInDimension.Participants.DEPOSIT_INTO_FSP.name())),
        new Amounts(Set.of(FundInDimension.Amounts.LIQUIDITY_AMOUNT.name()))),

    FUND_OUT(
        new Participants(Set.of(FundOutDimension.Participants.WITHDRAW_FROM_FSP.name())),
        new Amounts(Set.of(FundOutDimension.Amounts.LIQUIDITY_AMOUNT.name()))),

    FUND_TRANSFER(
        new Participants(Set.of(
            FundTransferCommitDimension.Participants.PAYER_FSP.name(),
            FundTransferCommitDimension.Participants.PAYEE_FSP.name())), new Amounts(Set.of(
        FundTransferCommitDimension.Amounts.TRANSFER_AMOUNT.name(),
        FundTransferCommitDimension.Amounts.PAYEE_FSP_FEE.name(),
        FundTransferCommitDimension.Amounts.PAYEE_FSP_COMMISSION.name())));

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
