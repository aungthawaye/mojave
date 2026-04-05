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

package org.mojave.core.scheme.rule.data;

import org.mojave.core.scheme.rule.type.TransactionType;

import java.util.Objects;
import java.util.Set;

public record TransactionTypeDefinitionData(TransactionType transactionType,
                                            Set<String> participants,
                                            Set<String> amountNames) {

    public TransactionTypeDefinitionData(final TransactionType transactionType,
                                         final Set<String> participants,
                                         final Set<String> amountNames) {

        Objects.requireNonNull(transactionType);
        Objects.requireNonNull(participants);
        Objects.requireNonNull(amountNames);

        this.transactionType = transactionType;
        this.participants = Set.copyOf(participants);
        this.amountNames = Set.copyOf(amountNames);
    }

    public boolean containsAmountName(final String amountName) {

        return this.amountNames.contains(amountName);
    }

    public boolean containsParticipant(final String participant) {

        return this.participants.contains(participant);
    }

}
