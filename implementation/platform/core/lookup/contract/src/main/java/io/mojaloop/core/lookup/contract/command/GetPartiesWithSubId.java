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
package io.mojaloop.core.lookup.contract.command;

import io.mojaloop.fspiop.service.component.FspiopHttpRequest;
import io.mojaloop.fspiop.common.type.Destination;
import io.mojaloop.fspiop.common.type.Source;

public interface GetPartiesWithSubId {

    GetParties.Output execute(GetParties.Input input);

    record Input(Source source,
                 Destination destination,
                 String partyIdType,
                 String partyId,
                 String subId,
                 FspiopHttpRequest fspiopHttpRequest) { }

    record Output() { }
}
