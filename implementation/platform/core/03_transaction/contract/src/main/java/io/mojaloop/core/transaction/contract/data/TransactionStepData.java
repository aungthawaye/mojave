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

package io.mojaloop.core.transaction.contract.data;

import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionStepId;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record TransactionStepData(TransactionStepId stepId,
                                  String name,
                                  StepPhase phase,
                                  Instant createdAt,
                                  TransactionId transactionId,
                                  List<StepParamData> params) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof TransactionStepData that)) {
            return false;
        }
        return Objects.equals(stepId, that.stepId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(stepId);
    }

}
