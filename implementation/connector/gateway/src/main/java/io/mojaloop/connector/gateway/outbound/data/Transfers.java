package io.mojaloop.connector.gateway.outbound.data;

import io.mojaloop.fspiop.common.type.Payee;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import io.mojaloop.fspiop.spec.core.TransfersIDPutResponse;
import io.mojaloop.fspiop.spec.core.TransfersPostRequest;

public record Transfers() {

    public record Request(Payee payee, TransfersPostRequest request) { }

    public record Response(Payee payee, String transferId, TransfersIDPutResponse response) { }

    public record Error(Payee payee,
                        String transferId,
                        ErrorInformationObject errorInformationObject) { }

}
