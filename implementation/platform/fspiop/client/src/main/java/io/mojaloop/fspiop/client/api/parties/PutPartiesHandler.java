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

package io.mojaloop.fspiop.client.api.parties;

import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.fspiop.client.service.LookUpService;
import io.mojaloop.fspiop.common.component.FspiopHeaders;
import io.mojaloop.fspiop.common.support.Destination;
import io.mojaloop.fspiop.common.support.ParticipantDetails;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import io.mojaloop.fspiop.spec.core.ErrorInformationResponse;
import io.mojaloop.fspiop.spec.core.PartiesTypeIDPutResponse;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import org.springframework.stereotype.Service;

@Service
class PutPartiesHandler implements PutParties {

    private final ParticipantDetails participantDetails;

    private final LookUpService lookUpService;

    private final RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder;

    public PutPartiesHandler(ParticipantDetails participantDetails,
                             LookUpService lookUpService,
                             RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder) {

        assert participantDetails != null;
        assert lookUpService != null;
        assert errorDecoder != null;

        this.participantDetails = participantDetails;
        this.lookUpService = lookUpService;
        this.errorDecoder = errorDecoder;
    }

    @Override
    public void putParties(Destination destination,
                           PartyIdType partyIdType,
                           String partyId,
                           PartiesTypeIDPutResponse partiesTypeIDPutResponse) {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Parties.forPut(this.participantDetails.fspCode(),
                                                                    destination.destinationFspCode());

            RetrofitService.invoke(this.lookUpService.putParty(fspiopHeaders, partyIdType, partyId, partiesTypeIDPutResponse),
                                   this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putParties(Destination destination,
                           PartyIdType partyIdType,
                           String partyId,
                           String subId,
                           PartiesTypeIDPutResponse partiesTypeIDPutResponse) {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Parties.forPut(this.participantDetails.fspCode(),
                                                                    destination.destinationFspCode());

            RetrofitService.invoke(this.lookUpService.putParty(fspiopHeaders, partyIdType, partyId, subId, partiesTypeIDPutResponse),
                                   this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putPartiesError(Destination destination,
                                PartyIdType partyIdType,
                                String partyId,
                                ErrorInformationObject errorInformationObject) {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Parties.forPut(this.participantDetails.fspCode(),
                                                                    destination.destinationFspCode());

            RetrofitService.invoke(this.lookUpService.putPartyError(fspiopHeaders, partyIdType, partyId, errorInformationObject),
                                   this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putPartiesError(Destination destination,
                                PartyIdType partyIdType,
                                String partyId,
                                String subId,
                                ErrorInformationObject errorInformationObject) {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Parties.forPut(this.participantDetails.fspCode(),
                                                                    destination.destinationFspCode());

            RetrofitService.invoke(this.lookUpService.putPartyError(fspiopHeaders, partyIdType, partyId, subId, errorInformationObject),
                                   this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }

}
