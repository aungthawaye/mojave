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
package io.mojaloop.common.fspiop.client.api.parties;

import io.mojaloop.common.fspiop.model.core.ErrorInformationObject;
import io.mojaloop.common.fspiop.model.core.PartiesTypeIDPutResponse;
import io.mojaloop.common.fspiop.support.Destination;

public interface PutParties {

    void putParties(Destination destination, String partyIdType, String partyId, PartiesTypeIDPutResponse partiesTypeIDPutResponse);

    void putParties(Destination destination,
                    String partyIdType,
                    String partyId,
                    String subId,
                    PartiesTypeIDPutResponse partiesTypeIDPutResponse);

    void putPartiesError(Destination destination, String partyIdType, String partyId, ErrorInformationObject errorInformationObject);

    void putPartiesError(Destination destination,
                         String partyIdType,
                         String partyId,
                         String subId,
                         ErrorInformationObject errorInformationObject);

}
