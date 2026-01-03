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
package org.mojave.rail.fspiop.invoker.api.parties;

import org.mojave.component.retrofit.RetrofitService;
import org.mojave.rail.fspiop.component.exception.FspiopException;
import org.mojave.rail.fspiop.component.participant.ParticipantContext;
import org.mojave.rail.fspiop.component.type.Payer;
import org.mojave.rail.fspiop.component.handy.FspiopHeaders;
import org.mojave.rail.fspiop.component.retrofit.FspiopErrorDecoder;
import org.mojave.rail.fspiop.component.retrofit.FspiopInvocationExceptionResolver;
import org.mojave.rail.fspiop.invoker.api.PartiesService;
import org.mojave.scheme.fspiop.core.ErrorInformationObject;
import org.mojave.scheme.fspiop.core.PartiesTypeIDPutResponse;
import org.mojave.scheme.fspiop.core.PartyIdType;
import org.springframework.stereotype.Service;

@Service
class PutPartiesHandler implements PutParties {

    private final ParticipantContext participantContext;

    private final PartiesService partiesService;

    private final FspiopErrorDecoder fspiopErrorDecoder;

    public PutPartiesHandler(ParticipantContext participantContext,
                             PartiesService partiesService,
                             FspiopErrorDecoder fspiopErrorDecoder) {

        assert participantContext != null;
        assert partiesService != null;
        assert fspiopErrorDecoder != null;

        this.participantContext = participantContext;
        this.partiesService = partiesService;
        this.fspiopErrorDecoder = fspiopErrorDecoder;

    }

    @Override
    public void putParties(Payer payer,
                           PartyIdType partyIdType,
                           String partyId,
                           PartiesTypeIDPutResponse partiesTypeIDPutResponse)
        throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Parties.forResult(
                this.participantContext.fspCode(), payer.fspCode());

            RetrofitService.invoke(
                this.partiesService.putParties(
                    fspiopHeaders, partyIdType, partyId,
                    partiesTypeIDPutResponse), this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw FspiopInvocationExceptionResolver.resolve(e);
        }
    }

    @Override
    public void putParties(Payer payer,
                           PartyIdType partyIdType,
                           String partyId,
                           String subId,
                           PartiesTypeIDPutResponse partiesTypeIDPutResponse)
        throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Parties.forResult(
                this.participantContext.fspCode(), payer.fspCode());

            RetrofitService.invoke(
                this.partiesService.putParties(
                    fspiopHeaders, partyIdType, partyId, subId,
                    partiesTypeIDPutResponse), this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw FspiopInvocationExceptionResolver.resolve(e);
        }
    }

    @Override
    public void putPartiesError(Payer payer,
                                PartyIdType partyIdType,
                                String partyId,
                                ErrorInformationObject errorInformationObject)
        throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Parties.forResult(
                this.participantContext.fspCode(), payer.fspCode());

            RetrofitService.invoke(
                this.partiesService.putPartiesError(
                    fspiopHeaders, partyIdType, partyId,
                    errorInformationObject), this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw FspiopInvocationExceptionResolver.resolve(e);
        }
    }

    @Override
    public void putPartiesError(Payer payer,
                                PartyIdType partyIdType,
                                String partyId,
                                String subId,
                                ErrorInformationObject errorInformationObject)
        throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Parties.forResult(
                this.participantContext.fspCode(), payer.fspCode());

            RetrofitService.invoke(
                this.partiesService.putPartiesError(
                    fspiopHeaders, partyIdType, partyId, subId,
                    errorInformationObject), this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw FspiopInvocationExceptionResolver.resolve(e);
        }
    }

}
