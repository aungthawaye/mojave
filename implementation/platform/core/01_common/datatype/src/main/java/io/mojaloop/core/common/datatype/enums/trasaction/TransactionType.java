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

import java.util.Set;

public enum TransactionType {

    FUND_IN(new Definition(TransactionPartyType.VOID, TransactionPartyType.FSP, Set.of("LIQUIDITY_AMOUNT"))),
    FUND_OUT(new Definition(TransactionPartyType.FSP, TransactionPartyType.VOID, Set.of("LIQUIDITY_AMOUNT"))),
    FUND_TRANSFER(new Definition(TransactionPartyType.FSP, TransactionPartyType.FSP, Set.of("TRANSFER_AMOUNT", "PAYEE_FSP_FEE", "PAYEE_FSP_COMMISSION")));

    @Getter
    private final Definition definition;

    TransactionType(Definition definition) {

        this.definition = definition;
    }

    public record Definition(TransactionPartyType source, TransactionPartyType destination, Set<String> amounts) { }
}
