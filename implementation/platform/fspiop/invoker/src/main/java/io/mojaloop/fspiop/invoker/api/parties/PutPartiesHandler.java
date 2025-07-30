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

package io.mojaloop.fspiop.invoker.api.parties;

import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.fspiop.component.retrofit.FspiopErrorDecoder;
import io.mojaloop.fspiop.invoker.api.PartiesService;
import io.mojaloop.fspiop.common.handy.FspiopHeaders;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Destination;
import io.mojaloop.fspiop.common.data.ParticipantDetails;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import io.mojaloop.fspiop.spec.core.ErrorInformationResponse;
import io.mojaloop.fspiop.spec.core.PartiesTypeIDPutResponse;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import org.springframework.stereotype.Service;

@Service
class PutPartiesHandler implements PutParties {

    private final ParticipantDetails participantDetails;

    private final PartiesService partiesService;

    private final FspiopErrorDecoder fspiopErrorDecoder;

    public PutPartiesHandler(ParticipantDetails participantDetails,
                             PartiesService partiesService,
                             FspiopErrorDecoder fspiopErrorDecoder) {

        assert participantDetails != null;
        assert partiesService != null;
        assert fspiopErrorDecoder != null;

        this.participantDetails = participantDetails;
        this.partiesService = partiesService;
        this.fspiopErrorDecoder = fspiopErrorDecoder;
    }

    @Override
    public void putParties(Destination destination,
                           PartyIdType partyIdType,
                           String partyId,
                           PartiesTypeIDPutResponse partiesTypeIDPutResponse) throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Parties.forResult(this.participantDetails.fspCode(),
                                                                       destination.destinationFspCode());

            RetrofitService.invoke(this.partiesService.putParties(fspiopHeaders, partyIdType, partyId, partiesTypeIDPutResponse),
                                   this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw new FspiopException(FspiopErrors.GENERIC_CLIENT_ERROR, e);
        }
    }

    @Override
    public void putParties(Destination destination,
                           PartyIdType partyIdType,
                           String partyId,
                           String subId,
                           PartiesTypeIDPutResponse partiesTypeIDPutResponse) throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Parties.forResult(this.participantDetails.fspCode(),
                                                                       destination.destinationFspCode());

            RetrofitService.invoke(this.partiesService.putParties(fspiopHeaders, partyIdType, partyId, subId, partiesTypeIDPutResponse),
                                   this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw new FspiopException(FspiopErrors.GENERIC_CLIENT_ERROR, e);
        }
    }

    @Override
    public void putPartiesError(Destination destination,
                                PartyIdType partyIdType,
                                String partyId,
                                ErrorInformationObject errorInformationObject) throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Parties.forResult(this.participantDetails.fspCode(),
                                                                       destination.destinationFspCode());

            RetrofitService.invoke(this.partiesService.putPartiesError(fspiopHeaders, partyIdType, partyId, errorInformationObject),
                                   this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw new FspiopException(FspiopErrors.GENERIC_CLIENT_ERROR, e);
        }
    }

    @Override
    public void putPartiesError(Destination destination,
                                PartyIdType partyIdType,
                                String partyId,
                                String subId,
                                ErrorInformationObject errorInformationObject) throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Parties.forResult(this.participantDetails.fspCode(),
                                                                       destination.destinationFspCode());

            RetrofitService.invoke(this.partiesService.putPartiesError(fspiopHeaders, partyIdType, partyId, subId, errorInformationObject),
                                   this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw new FspiopException(FspiopErrors.GENERIC_CLIENT_ERROR, e);
        }
    }

}
