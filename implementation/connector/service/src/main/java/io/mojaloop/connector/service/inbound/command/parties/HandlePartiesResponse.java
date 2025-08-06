package io.mojaloop.connector.service.inbound.command.parties;

import io.mojaloop.fspiop.common.type.Source;
import io.mojaloop.fspiop.spec.core.PartiesTypeIDPutResponse;
import io.mojaloop.fspiop.spec.core.PartyIdType;

public interface HandlePartiesResponse {

    Output execute(Input input);

    record Input(Source source, PartyIdType partyIdType, String partyId, String subId, PartiesTypeIDPutResponse response) { }

    record Output() { }

}
