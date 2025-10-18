/*-
 * ================================================================================
 * Mojaloop OSS
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

    FUND_IN(new Participants(Set.of("FSP")), new Amounts(Set.of("LIQUIDITY_AMOUNT"))),
    FUND_OUT(new Participants(Set.of("FSP")), new Amounts(Set.of("LIQUIDITY_AMOUNT"))),
    FUND_TRANSFER(new Participants(Set.of("PAYER_FSP", "PAYEE_FSP")), new Amounts(Set.of("TRANSFER_AMOUNT", "PAYEE_FSP_FEE", "PAYEE_FSP_COMMISSION")));

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
