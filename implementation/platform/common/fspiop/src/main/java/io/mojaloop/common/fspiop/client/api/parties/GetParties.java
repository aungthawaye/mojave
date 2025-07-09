package io.mojaloop.common.fspiop.client.api.parties;

import io.mojaloop.common.fspiop.support.Destination;

public interface GetParties {

    void getParties(Destination destination, String partyIdType, String partyId, String subId);

    void getParties(Destination destination, String partyIdType, String partyId);

}
