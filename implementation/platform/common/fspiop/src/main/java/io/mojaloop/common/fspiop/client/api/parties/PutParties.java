package io.mojaloop.common.fspiop.client.api.parties;

import io.mojaloop.common.fspiop.core.model.ErrorInformationObject;
import io.mojaloop.common.fspiop.core.model.PartiesTypeIDPutResponse;
import io.mojaloop.common.fspiop.support.Destination;

public interface PutParties {

    void putParties(Destination destination, String partyIdType, String partyId, PartiesTypeIDPutResponse partiesTypeIDPutResponse);

    void putParties(Destination destination,
                    String partyIdType,
                    String partyId,
                    String subId,
                    PartiesTypeIDPutResponse partiesTypeIDPutResponse);

    void putPartiesError(Destination destination, String partyIdType, String partyId, ErrorInformationObject errorInformationObject);

    void putPartiesError(Destination destination,
                         String partyIdType,
                         String partyId,
                         String subId,
                         ErrorInformationObject errorInformationObject);

}
