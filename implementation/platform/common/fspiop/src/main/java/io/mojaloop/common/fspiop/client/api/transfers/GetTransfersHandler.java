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
package io.mojaloop.common.fspiop.client.api.transfers;

import io.mojaloop.common.component.retrofit.RetrofitService;
import io.mojaloop.common.fspiop.component.FspiopHeaders;
import io.mojaloop.common.fspiop.model.core.ErrorInformationResponse;
import io.mojaloop.common.fspiop.service.TransferService;
import io.mojaloop.common.fspiop.support.Destination;
import io.mojaloop.common.fspiop.support.ParticipantSettings;
import org.springframework.stereotype.Service;

@Service
class GetTransfersHandler implements GetTransfers {

    private final ParticipantSettings participantSettings;

    private final TransferService transferService;

    private final RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder;

    public GetTransfersHandler(ParticipantSettings participantSettings,
                               TransferService transferService,
                               RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder) {

        assert participantSettings != null;
        assert transferService != null;
        assert errorDecoder != null;

        this.participantSettings = participantSettings;
        this.transferService = transferService;
        this.errorDecoder = errorDecoder;
    }

    @Override
    public void getTransfers(Destination destination, String transferId) {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Transfers.forRequest(this.participantSettings.fspId(), destination.destinationFspId());

            RetrofitService.invoke(this.transferService.getTransfers(fspiopHeaders, transferId), this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }
}
