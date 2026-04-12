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

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.mojave.core.accounting.contract.data.CoaEntryData;
import org.mojave.core.accounting.contract.exception.chart.CoaEntryIdNotFoundException;
import org.mojave.scheme.rule.identifier.accounting.CoaEntryId;
import org.mojave.scheme.rule.identifier.accounting.CoaId;

import java.util.List;

public interface CoaEntryQuery {

    String GET_BY_ID_SUBJECT_NAME = "sub-accounting.coa-entry-query.get-by-id";

    String GET_BY_COA_ID_SUBJECT_NAME = "sub-accounting.coa-entry-query.get-by-coa-id";

    String GET_BY_CATEGORY_SUBJECT_NAME = "sub-accounting.coa-entry-query.get-by-category";

    String GET_ALL_SUBJECT_NAME = "sub-accounting.coa-entry-query.get-all";

    CoaEntryData get(CoaEntryId coaEntryId) throws CoaEntryIdNotFoundException;

    List<CoaEntryData> get(CoaId coaId);

    List<CoaEntryData> get(String category);

    List<CoaEntryData> getAll();

    record GetByIdInput(@JsonProperty(required = true) @NotNull CoaEntryId coaEntryId) { }

    record GetByCoaIdInput(@JsonProperty(required = true) @NotNull CoaId coaId) { }

    record GetByCategoryInput(@JsonProperty(required = true) @NotNull String category) { }

    record GetAllInput() { }

}
