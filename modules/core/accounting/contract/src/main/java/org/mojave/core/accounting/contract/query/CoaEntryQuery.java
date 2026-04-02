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

package org.mojave.core.accounting.contract.query;

import org.mojave.common.datatype.enums.accounting.ChartEntryCategory;
import org.mojave.common.datatype.identifier.accounting.CoaEntryId;
import org.mojave.common.datatype.identifier.accounting.CoaId;
import org.mojave.core.accounting.contract.data.CoaEntryData;
import org.mojave.core.accounting.contract.exception.chart.CoaEntryIdNotFoundException;

import java.util.List;

public interface CoaEntryQuery {

    CoaEntryData get(CoaEntryId coaEntryId) throws CoaEntryIdNotFoundException;

    List<CoaEntryData> get(CoaId coaId);

    List<CoaEntryData> get(ChartEntryCategory category);

    List<CoaEntryData> get(String name);

    List<CoaEntryData> getAll();

}
