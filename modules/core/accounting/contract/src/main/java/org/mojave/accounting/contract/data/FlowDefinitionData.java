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

package org.mojave.accounting.contract.data;

import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.TerminationStatus;
import org.mojave.common.datatype.enums.accounting.Side;
import org.mojave.common.datatype.identifier.accounting.CoaEntryId;
import org.mojave.common.datatype.identifier.accounting.FlowDefinitionId;
import org.mojave.common.datatype.identifier.accounting.FlowDefinitionLineId;
import org.mojave.scheme.rule.accounting.scenario.ScenarioType;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public record FlowDefinitionData(FlowDefinitionId flowDefinitionId,
                                 ScenarioType scenario,
                                 Currency currency,
                                 String name,
                                 String description,
                                 ActivationStatus activationStatus,
                                 TerminationStatus terminationStatus,
                                 List<FlowDefinitionLineData> flowDefinitionLines) {

    public FlowDefinitionData(FlowDefinitionId flowDefinitionId,
                              ScenarioType scenario,
                              Currency currency,
                              String name,
                              String description,
                              ActivationStatus activationStatus,
                              TerminationStatus terminationStatus,
                              List<FlowDefinitionLineData> flowDefinitionLines) {

        this.flowDefinitionId = flowDefinitionId;
        this.scenario = scenario;
        this.currency = currency;
        this.name = name;
        this.description = description;
        this.activationStatus = activationStatus;
        this.terminationStatus = terminationStatus;
        this.flowDefinitionLines = flowDefinitionLines
                             .stream()
                             .sorted(Comparator.comparing(FlowDefinitionLineData::step))
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

    public record FlowDefinitionLineData(FlowDefinitionLineId flowDefinitionLineId,
                               Integer step,
                               String participant,
                               CoaEntryId coaEntryId,
                               String amountName,
                               Side side,
                               String description) {

        @Override
        public boolean equals(Object o) {

            if (!(o instanceof FlowDefinitionLineData that)) {
                return false;
            }

            return Objects.equals(flowDefinitionLineId, that.flowDefinitionLineId);
        }

        @Override
        public int hashCode() {

            return Objects.hashCode(flowDefinitionLineId);
        }

    }

}
