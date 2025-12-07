package io.mojaloop.connector.gateway.outbound.data;

import io.mojaloop.fspiop.common.type.Payee;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import io.mojaloop.fspiop.spec.core.PartiesTypeIDPutResponse;
import io.mojaloop.fspiop.spec.core.PartyIdType;

public record Parties() {

    public record Request(Payee payee, PartyIdType partyIdType, String partyId, String subId) {

    }

    public record Response(Payee payee,
                           PartyIdType partyIdType,
                           String partyId,
                           String subId,
                           PartiesTypeIDPutResponse response) { }

    public record Error(Payee payee,
                        PartyIdType partyIdType,
                        String partyId,
                        String subId,
                        ErrorInformationObject errorInformationObject) { }

}
