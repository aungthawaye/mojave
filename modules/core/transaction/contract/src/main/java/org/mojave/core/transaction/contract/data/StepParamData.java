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
package org.mojave.core.transaction.contract.data;

import org.mojave.scheme.common.datatype.identifier.transaction.StepParamId;
import org.mojave.scheme.common.datatype.identifier.transaction.TransactionStepId;

import java.util.Objects;

public record StepParamData(StepParamId paramId,
                            String name,
                            String value,
                            TransactionStepId stepId) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof StepParamData that)) {
            return false;
        }
        return Objects.equals(paramId, that.paramId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(paramId);
    }

}
