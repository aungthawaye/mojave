package io.mojaloop.common.fspiop.client.api.parties;

import io.mojaloop.common.component.retrofit.RetrofitService;
import io.mojaloop.common.fspiop.component.FspiopHeaders;
import io.mojaloop.common.fspiop.core.model.ErrorInformationResponse;
import io.mojaloop.common.fspiop.service.AccountLookUpService;
import io.mojaloop.common.fspiop.support.Destination;
import io.mojaloop.common.fspiop.support.FspSettings;
import org.springframework.stereotype.Service;

@Service
class GetPartiesHandler implements GetParties {

    private final FspSettings fspSettings;

    private final AccountLookUpService accountLookUpService;

    private final RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder;

    public GetPartiesHandler(FspSettings fspSettings,
                             AccountLookUpService accountLookUpService,
                             RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder) {

        assert fspSettings != null;
        assert accountLookUpService != null;
        assert errorDecoder != null;

        this.fspSettings = fspSettings;
        this.accountLookUpService = accountLookUpService;
        this.errorDecoder = errorDecoder;
    }

    @Override
    public void getParties(Destination destination, String partyIdType, String partyId, String subId) {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Parties.forRequest(this.fspSettings.fspId(), destination.destinationFspId());

            RetrofitService.invoke(this.accountLookUpService.getParty(fspiopHeaders, partyIdType, partyId), this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getParties(Destination destination, String partyIdType, String partyId) {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Parties.forRequest(this.fspSettings.fspId(), destination.destinationFspId());

            RetrofitService.invoke(this.accountLookUpService.getParty(fspiopHeaders, partyIdType, partyId), null);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }

}
