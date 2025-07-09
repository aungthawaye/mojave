package io.mojaloop.common.fspiop.client.api.transfers;

import io.mojaloop.common.component.retrofit.RetrofitService;
import io.mojaloop.common.fspiop.component.FspiopHeaders;
import io.mojaloop.common.fspiop.core.model.ErrorInformationResponse;
import io.mojaloop.common.fspiop.core.model.TransfersPostRequest;
import io.mojaloop.common.fspiop.service.TransferService;
import io.mojaloop.common.fspiop.support.Destination;
import io.mojaloop.common.fspiop.support.FspSettings;
import org.springframework.stereotype.Service;

@Service
class PostTransfersHandler implements PostTransfers {

    private final FspSettings fspSettings;

    private final TransferService transferService;

    private final RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder;

    public PostTransfersHandler(FspSettings fspSettings,
                             TransferService transferService,
                             RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder) {

        assert fspSettings != null;
        assert transferService != null;
        assert errorDecoder != null;

        this.fspSettings = fspSettings;
        this.transferService = transferService;
        this.errorDecoder = errorDecoder;
    }

    @Override
    public void postTransfers(Destination destination, TransfersPostRequest transfersPostRequest) {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Transfers.forRequest(this.fspSettings.fspId(), destination.destinationFspId());

            RetrofitService.invoke(this.transferService.postTransfers(fspiopHeaders, transfersPostRequest), this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }
}