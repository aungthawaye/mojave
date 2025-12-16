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
package org.mojave.fspiop.invoker.api.transfers;

import org.mojave.component.retrofit.RetrofitService;
import org.mojave.fspiop.component.exception.FspiopException;
import org.mojave.fspiop.component.participant.ParticipantContext;
import org.mojave.fspiop.component.type.Payer;
import org.mojave.fspiop.component.handy.FspiopHeaders;
import org.mojave.fspiop.component.retrofit.FspiopErrorDecoder;
import org.mojave.fspiop.component.retrofit.FspiopInvocationExceptionResolver;
import org.mojave.fspiop.invoker.api.TransfersService;
import org.mojave.fspiop.spec.core.ErrorInformationObject;
import org.mojave.fspiop.spec.core.TransfersIDPutResponse;
import org.springframework.stereotype.Service;

@Service
class PutTransfersHandler implements PutTransfers {

    private final ParticipantContext participantContext;

    private final TransfersService transfersService;

    private final FspiopErrorDecoder fspiopErrorDecoder;

    public PutTransfersHandler(ParticipantContext participantContext,
                               TransfersService transfersService,
                               FspiopErrorDecoder fspiopErrorDecoder) {

        assert participantContext != null;
        assert transfersService != null;
        assert fspiopErrorDecoder != null;

        this.participantContext = participantContext;
        this.transfersService = transfersService;
        this.fspiopErrorDecoder = fspiopErrorDecoder;
    }

    @Override
    public void putTransfers(Payer payer,
                             String transferId,
                             TransfersIDPutResponse transfersIDPutResponse) throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Transfers.forResult(
                this.participantContext.fspCode(), payer.fspCode());

            RetrofitService.invoke(
                this.transfersService.putTransfers(
                    fspiopHeaders, transferId, transfersIDPutResponse), this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw FspiopInvocationExceptionResolver.resolve(e);
        }
    }

    @Override
    public void putTransfersError(Payer payer,
                                  String transferId,
                                  ErrorInformationObject errorInformationObject)
        throws FspiopException {

        try {
            var fspiopHeaders = FspiopHeaders.Values.Transfers.forResult(
                this.participantContext.fspCode(), payer.fspCode());

            RetrofitService.invoke(
                this.transfersService.putTransfersError(
                    fspiopHeaders, transferId, errorInformationObject), this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw FspiopInvocationExceptionResolver.resolve(e);
        }
    }

}
