package io.mojaloop.common.fspiop.client.api.parties;

import io.mojaloop.common.component.retrofit.RetrofitService;
import io.mojaloop.common.fspiop.component.FspiopHeaders;
import io.mojaloop.common.fspiop.model.core.ErrorInformationObject;
import io.mojaloop.common.fspiop.model.core.ErrorInformationResponse;
import io.mojaloop.common.fspiop.model.core.PartiesTypeIDPutResponse;
import io.mojaloop.common.fspiop.service.AccountLookUpService;
import io.mojaloop.common.fspiop.support.Destination;
import io.mojaloop.common.fspiop.support.ParticipantSettings;
import org.springframework.stereotype.Service;

@Service
class PutPartiesHandler implements PutParties {

    private final ParticipantSettings participantSettings;

    private final AccountLookUpService accountLookUpService;

    private final RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder;

    public PutPartiesHandler(ParticipantSettings participantSettings,
                             AccountLookUpService accountLookUpService,
                             RetrofitService.ErrorDecoder<ErrorInformationResponse> errorDecoder) {

        assert participantSettings != null;
        assert accountLookUpService != null;
        assert errorDecoder != null;

        this.participantSettings = participantSettings;
        this.accountLookUpService = accountLookUpService;
        this.errorDecoder = errorDecoder;
    }

    @Override
    public void putParties(Destination destination, String partyIdType, String partyId, PartiesTypeIDPutResponse partiesTypeIDPutResponse) {

        try {

            var fspiopHeaders = FspiopHeaders.Values.Parties.forCallback(this.participantSettings.fspId(), destination.destinationFspId());

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

            var fspiopHeaders = FspiopHeaders.Values.Parties.forCallback(this.participantSettings.fspId(), destination.destinationFspId());

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

            var fspiopHeaders = FspiopHeaders.Values.Parties.forCallback(this.participantSettings.fspId(), destination.destinationFspId());

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

            var fspiopHeaders = FspiopHeaders.Values.Parties.forCallback(this.participantSettings.fspId(), destination.destinationFspId());

            RetrofitService.invoke(
                this.accountLookUpService.putPartyError(fspiopHeaders, partyIdType, partyId, subId, errorInformationObject),
                this.errorDecoder);

        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }

}
