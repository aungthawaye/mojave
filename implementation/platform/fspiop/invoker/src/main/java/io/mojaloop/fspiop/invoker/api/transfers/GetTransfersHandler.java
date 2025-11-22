/*-
 * ================================================================================
 * Mojave
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

package io.mojaloop.fspiop.invoker.api.transfers;

import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.participant.ParticipantContext;
import io.mojaloop.fspiop.common.type.Payee;
import io.mojaloop.fspiop.component.handy.FspiopHeaders;
import io.mojaloop.fspiop.component.retrofit.FspiopErrorDecoder;
import io.mojaloop.fspiop.component.retrofit.FspiopInvocationExceptionResolver;
import io.mojaloop.fspiop.invoker.api.TransfersService;
import org.springframework.stereotype.Service;

@Service
class GetTransfersHandler implements GetTransfers {

    private final ParticipantContext participantContext;

    private final TransfersService transfersService;

    private final FspiopErrorDecoder fspiopErrorDecoder;

    public GetTransfersHandler(ParticipantContext participantContext, TransfersService transfersService, FspiopErrorDecoder fspiopErrorDecoder) {

        assert participantContext != null;
        assert transfersService != null;
        assert fspiopErrorDecoder != null;

        this.participantContext = participantContext;
        this.transfersService = transfersService;
        this.fspiopErrorDecoder = fspiopErrorDecoder;
    }

    @Override
    public void getTransfers(Payee payee, String transferId) throws FspiopException {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Transfers.forRequest(this.participantContext.fspCode(), payee.fspCode());

            RetrofitService.invoke(this.transfersService.getTransfers(fspiopHeaders, transferId), this.fspiopErrorDecoder);

        } catch (RetrofitService.InvocationException e) {

            throw FspiopInvocationExceptionResolver.resolve(e);
        }
    }

}
