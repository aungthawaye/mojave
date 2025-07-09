package io.mojaloop.common.fspiop.client.api.parties;

import io.mojaloop.common.component.retrofit.RetrofitService;
import io.mojaloop.common.fspiop.component.FspiopHeaders;
import io.mojaloop.common.fspiop.core.model.ErrorInformationObject;
import io.mojaloop.common.fspiop.core.model.ErrorInformationResponse;
import io.mojaloop.common.fspiop.core.model.PartiesTypeIDPutResponse;
import io.mojaloop.common.fspiop.service.AccountLookUpService;
import io.mojaloop.common.fspiop.support.Destination;
import io.mojaloop.common.fspiop.support.FspSettings;
import org.springframework.stereotype.Service;

@Service
class PutPartiesHandler implements PutParties {

    private final FspSettings fspSettings;

    private final AccountLookUpService accountLookUpService;

    private final RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder;

    public PutPartiesHandler(FspSettings fspSettings,
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
    public void putParties(Destination destination, String partyIdType, String partyId, PartiesTypeIDPutResponse partiesTypeIDPutResponse) {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Parties.forCallback(this.fspSettings.fspId(), destination.destinationFspId());

            RetrofitService.invoke(this.accountLookUpService.putParty(fspiopHeaders, partyIdType, partyId, partiesTypeIDPutResponse),
                                   this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putParties(Destination destination,
                           String partyIdType,
                           String partyId,
                           String subId,
                           PartiesTypeIDPutResponse partiesTypeIDPutResponse) {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Parties.forCallback(this.fspSettings.fspId(), destination.destinationFspId());

            RetrofitService.invoke(this.accountLookUpService.putParty(fspiopHeaders, partyIdType, partyId, subId, partiesTypeIDPutResponse),
                                   this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putPartiesError(Destination destination,
                                String partyIdType,
                                String partyId,
                                ErrorInformationObject errorInformationObject) {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Parties.forCallback(this.fspSettings.fspId(), destination.destinationFspId());

            RetrofitService.invoke(this.accountLookUpService.putPartyError(fspiopHeaders, partyIdType, partyId, errorInformationObject),
                                   this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putPartiesError(Destination destination,
                                String partyIdType,
                                String partyId,
                                String subId,
                                ErrorInformationObject errorInformationObject) {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Parties.forCallback(this.fspSettings.fspId(), destination.destinationFspId());

            RetrofitService.invoke(
                this.accountLookUpService.putPartyError(fspiopHeaders, partyIdType, partyId, subId, errorInformationObject),
                this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }

}
