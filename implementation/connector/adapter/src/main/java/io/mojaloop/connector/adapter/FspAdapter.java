package io.mojaloop.connector.adapter;

import io.mojaloop.fspiop.spec.core.PartiesTypeIDPutResponse;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import io.mojaloop.fspiop.spec.core.QuotesIDPutResponse;
import io.mojaloop.fspiop.spec.core.QuotesPostRequest;
import io.mojaloop.fspiop.spec.core.TransfersIDPatchResponse;
import io.mojaloop.fspiop.spec.core.TransfersIDPutResponse;
import io.mojaloop.fspiop.spec.core.TransfersPostRequest;

public interface FspAdapter {

    PartiesTypeIDPutResponse getParties(PartyIdType partyIdType, String partyId, String subId);

    TransfersIDPutResponse initiateTransfer(TransfersPostRequest request);

    void notifyTransfer(TransfersIDPatchResponse response);

    QuotesIDPutResponse quote(QuotesPostRequest request);

}
