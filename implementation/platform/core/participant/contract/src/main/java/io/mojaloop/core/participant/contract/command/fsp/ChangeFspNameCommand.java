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

package io.mojaloop.core.participant.contract.command.fsp;

import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.participant.contract.exception.FspIdNotFoundException;

public interface ChangeFspNameCommand {

    Output execute(Input input) throws FspIdNotFoundException;

    record Input(FspId fspId, String name) { }

    record Output() { }

}
