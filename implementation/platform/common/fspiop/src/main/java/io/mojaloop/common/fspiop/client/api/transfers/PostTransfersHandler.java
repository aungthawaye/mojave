package io.mojaloop.common.fspiop.client.api.transfers;

import io.mojaloop.common.component.retrofit.RetrofitService;
import io.mojaloop.common.fspiop.component.FspiopHeaders;
import io.mojaloop.common.fspiop.model.core.ErrorInformationResponse;
import io.mojaloop.common.fspiop.model.core.TransfersPostRequest;
import io.mojaloop.common.fspiop.service.TransferService;
import io.mojaloop.common.fspiop.support.Destination;
import io.mojaloop.common.fspiop.support.ParticipantSettings;
import org.springframework.stereotype.Service;

@Service
class PostTransfersHandler implements PostTransfers {

    private final ParticipantSettings participantSettings;

    private final TransferService transferService;

    private final RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder;

    public PostTransfersHandler(ParticipantSettings participantSettings,
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
    public void postTransfers(Destination destination, TransfersPostRequest transfersPostRequest) {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Transfers.forRequest(this.participantSettings.fspId(), destination.destinationFspId());

            RetrofitService.invoke(this.transferService.postTransfers(fspiopHeaders, transfersPostRequest), this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }
}