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
package org.mojave.core.accounting.contract.data;

import org.mojave.core.common.datatype.enums.ActivationStatus;
import org.mojave.core.common.datatype.enums.TerminationStatus;
import org.mojave.core.common.datatype.enums.accounting.ReceiveIn;
import org.mojave.core.common.datatype.enums.accounting.Side;
import org.mojave.core.common.datatype.enums.trasaction.TransactionType;
import org.mojave.core.common.datatype.identifier.accounting.FlowDefinitionId;
import org.mojave.core.common.datatype.identifier.accounting.PostingDefinitionId;
import org.mojave.specification.fspiop.core.Currency;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public record FlowDefinitionData(FlowDefinitionId flowDefinitionId,
                                 TransactionType transactionType,
                                 Currency currency,
                                 String name,
                                 String description,
                                 ActivationStatus activationStatus,
                                 TerminationStatus terminationStatus,
                                 List<PostingDefinitionData> postings) {

    public FlowDefinitionData(FlowDefinitionId flowDefinitionId,
                              TransactionType transactionType,
                              Currency currency,
                              String name,
                              String description,
                              ActivationStatus activationStatus,
                              TerminationStatus terminationStatus,
                              List<PostingDefinitionData> postings) {

        this.flowDefinitionId = flowDefinitionId;
        this.transactionType = transactionType;
        this.currency = currency;
        this.name = name;
        this.description = description;
        this.activationStatus = activationStatus;
        this.terminationStatus = terminationStatus;
        this.postings = postings
                            .stream()
                            .sorted(Comparator.comparing(PostingDefinitionData::step))
                            .toList();
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof FlowDefinitionData that)) {
            return false;
        }
        return Objects.equals(flowDefinitionId, that.flowDefinitionId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(flowDefinitionId);
    }

    public record PostingDefinitionData(PostingDefinitionId postingDefinitionId,
                                        Integer step,
                                        ReceiveIn receiveIn,
                                        Long receiveInId,
                                        String participant,
                                        String amountName,
                                        Side side,
                                        String description) {

        @Override
        public boolean equals(Object o) {

            if (!(o instanceof PostingDefinitionData that)) {
                return false;
            }

            return Objects.equals(postingDefinitionId, that.postingDefinitionId);
        }

        @Override
        public int hashCode() {

            return Objects.hashCode(postingDefinitionId);
        }

    }

}

