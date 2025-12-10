package org.mojave.connector.gateway.outbound.data;

import org.mojave.fspiop.common.type.Payee;
import org.mojave.fspiop.spec.core.ErrorInformationObject;
import org.mojave.fspiop.spec.core.PartiesTypeIDPutResponse;
import org.mojave.fspiop.spec.core.PartyIdType;

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
