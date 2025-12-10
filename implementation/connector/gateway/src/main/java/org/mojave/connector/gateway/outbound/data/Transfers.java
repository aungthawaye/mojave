package org.mojave.connector.gateway.outbound.data;

import org.mojave.fspiop.common.type.Payee;
import org.mojave.fspiop.spec.core.ErrorInformationObject;
import org.mojave.fspiop.spec.core.TransfersIDPutResponse;
import org.mojave.fspiop.spec.core.TransfersPostRequest;

public record Transfers() {

    public record Request(Payee payee, TransfersPostRequest request) { }

    public record Response(Payee payee, String transferId, TransfersIDPutResponse response) { }

    public record Error(Payee payee,
                        String transferId,
                        ErrorInformationObject errorInformationObject) { }

}
