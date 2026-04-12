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

import org.mojave.core.participant.contract.data.HubData;

import java.util.List;

public interface HubQuery {

    String COUNT_SUBJECT_NAME = "sub-participant.hub-query.count";

    String GET_SUBJECT_NAME = "sub-participant.hub-query.get";

    String GET_ALL_SUBJECT_NAME = "sub-participant.hub-query.get-all";

    long count();

    HubData get();

    List<HubData> getAll();

    record CountInput() { }

    record GetInput() { }

    record GetAllInput() { }

}
