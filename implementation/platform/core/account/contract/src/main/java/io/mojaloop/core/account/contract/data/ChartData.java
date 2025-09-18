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
package io.mojaloop.core.account.contract.data;

import io.mojaloop.core.common.datatype.identifier.account.ChartId;

import java.time.Instant;
import java.util.Objects;

public record ChartData(ChartId chartId, String name, Instant createdAt, ChartEntryData[] entries) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof ChartData that)) {
            return false;
        }
        return Objects.equals(chartId, that.chartId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(chartId);
    }

}
