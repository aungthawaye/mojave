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

package io.mojaloop.core.participant.store.data;

import io.mojaloop.common.datatype.enumeration.fspiop.EndpointType;
import io.mojaloop.common.datatype.identifier.participant.FspId;
import io.mojaloop.common.datatype.type.fspiop.FspCode;
import io.mojaloop.common.fspiop.model.core.Currency;

import java.util.Map;

public record ParticipantData(FspId fspId,
                              FspCode fspCode,
                              Currency[] supportedCurrencies,
                              Map<EndpointType, EndpointData> endpoints) {

    public record EndpointData(EndpointType endpointType, String endpointUrl) { }

}
