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
package org.mojave.fspiop.service.api.transfers;

import org.mojave.component.retrofit.RetrofitService;
import org.mojave.fspiop.component.exception.FspiopException;
import org.mojave.fspiop.component.participant.ParticipantContext;
import org.mojave.fspiop.component.type.Payee;
import org.mojave.fspiop.component.type.Payer;
import org.mojave.fspiop.component.handy.FspiopHeaders;
import org.mojave.fspiop.component.retrofit.FspiopErrorDecoder;
import org.mojave.fspiop.component.retrofit.FspiopInvocationExceptionResolver;
import org.mojave.fspiop.service.api.TransfersResponseService;
import org.mojave.scheme.fspiop.core.ErrorInformationObject;
import org.mojave.scheme.fspiop.core.TransfersIDPatchResponse;
import org.mojave.scheme.fspiop.core.TransfersIDPutResponse;
import org.springframework.stereotype.Service;

@Service
public class RespondTransfersHandler implements RespondTransfers {

    private final ParticipantContext participantContext;

    private final TransfersResponseService transfersResponseService;

    private final FspiopErrorDecoder fspiopErrorDecoder;

    public RespondTransfersHandler(ParticipantContext participantContext,
                                   TransfersResponseService transfersResponseService,
                                   FspiopErrorDecoder fspiopErrorDecoder) {

        assert participantContext != null;
        assert transfersResponseService != null;
        assert fspiopErrorDecoder != null;

        this.participantContext = participantContext;
        this.transfersResponseService = transfersResponseService;
        this.fspiopErrorDecoder = fspiopErrorDecoder;

    }

    @Override
    public void patchTransfers(Payee payee, String url, TransfersIDPatchResponse response)
        throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Transfers.forResult(
                this.participantContext.fspCode(), payee.fspCode());

            RetrofitService.invoke(
                this.transfersResponseService.patchTransfers(url, fspiopHeaders, response),
                this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw FspiopInvocationExceptionResolver.resolve(e);
        }
    }

    @Override
    public void putTransfers(Payer payer, String url, TransfersIDPutResponse response)
        throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Transfers.forResult(
                this.participantContext.fspCode(), payer.fspCode());

            RetrofitService.invoke(
                this.transfersResponseService.putTransfers(url, fspiopHeaders, response),
                this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw FspiopInvocationExceptionResolver.resolve(e);
        }
    }

    @Override
    public void putTransfersError(Payer payer, String url, ErrorInformationObject error)
        throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Transfers.forResult(
                this.participantContext.fspCode(), payer.fspCode());

            RetrofitService.invoke(
                this.transfersResponseService.putTransfersError(url, fspiopHeaders, error),
                this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw FspiopInvocationExceptionResolver.resolve(e);
        }
    }

}
