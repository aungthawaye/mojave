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
package org.mojave.fspiop.invoker.api.parties;

import org.mojave.component.retrofit.RetrofitService;
import org.mojave.fspiop.component.exception.FspiopException;
import org.mojave.fspiop.component.participant.ParticipantContext;
import org.mojave.fspiop.component.type.Payee;
import org.mojave.fspiop.component.handy.FspiopHeaders;
import org.mojave.fspiop.component.retrofit.FspiopErrorDecoder;
import org.mojave.fspiop.component.retrofit.FspiopInvocationExceptionResolver;
import org.mojave.fspiop.invoker.api.PartiesService;
import org.mojave.scheme.fspiop.core.PartyIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
class GetPartiesHandler implements GetParties {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPartiesHandler.class);

    private final ParticipantContext participantContext;

    private final PartiesService partiesService;

    private final FspiopErrorDecoder fspiopErrorDecoder;

    public GetPartiesHandler(ParticipantContext participantContext,
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
    public void getParties(Payee payee, PartyIdType partyIdType, String partyId, String subId)
        throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Parties.forRequest(
                this.participantContext.fspCode(), payee.fspCode());

            RetrofitService.invoke(
                this.partiesService.getParties(fspiopHeaders, partyIdType, partyId),
                this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw FspiopInvocationExceptionResolver.resolve(e);
        }
    }

    @Override
    public void getParties(Payee payee, PartyIdType partyIdType, String partyId)
        throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Parties.forRequest(
                this.participantContext.fspCode(), payee.fspCode());

            RetrofitService.invoke(
                this.partiesService.getParties(fspiopHeaders, partyIdType, partyId),
                this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw FspiopInvocationExceptionResolver.resolve(e);
        }
    }

}
