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

package org.mojave.core.accounting.domain.cache;

import org.mojave.common.datatype.identifier.accounting.ChartEntryId;
import org.mojave.common.datatype.identifier.accounting.ChartId;
import org.mojave.common.datatype.type.accounting.ChartEntryCode;
import org.mojave.core.accounting.contract.data.ChartEntryData;

import java.util.Set;

public interface ChartEntryCache {

    void clear();

    void delete(ChartEntryId chartEntryId);

    ChartEntryData get(ChartEntryId chartEntryId);

    ChartEntryData get(ChartEntryCode code);

    Set<ChartEntryData> get(ChartId chartId);

    void save(ChartEntryData chartEntry);

    class Qualifiers {

        public static final String REDIS = "redis";

        public static final String IN_MEMORY = "in-memory";

        public static final String DEFAULT = REDIS;

    }

    class Names {

        public static final String WITH_ID = "acc-chart-entry-with-id";

        public static final String WITH_CODE = "acc-chart-entry-with-code";

    }

}
