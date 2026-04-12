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

package org.mojave.core.participant.contract.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.mojave.scheme.rule.enums.participant.PartyIdType;
import org.mojave.scheme.rule.identifier.participant.OracleId;
import org.mojave.core.participant.contract.data.OracleData;

import java.util.List;
import java.util.Optional;

public interface OracleQuery {

    String FIND_BY_TYPE_SUBJECT_NAME = "sub-participant.oracle-query.find-by-type";

    String GET_BY_TYPE_SUBJECT_NAME = "sub-participant.oracle-query.get-by-type";

    String GET_BY_ID_SUBJECT_NAME = "sub-participant.oracle-query.get-by-id";

    String GET_ALL_SUBJECT_NAME = "sub-participant.oracle-query.get-all";

    Optional<OracleData> find(PartyIdType type);

    OracleData get(PartyIdType type);

    OracleData get(OracleId oracleId);

    List<OracleData> getAll();

    record FindByTypeInput(@JsonProperty(required = true) @NotNull PartyIdType type) { }

    record GetByTypeInput(@JsonProperty(required = true) @NotNull PartyIdType type) { }

    record GetByIdInput(@JsonProperty(required = true) @NotNull OracleId oracleId) { }

    record GetAllInput() { }

}
