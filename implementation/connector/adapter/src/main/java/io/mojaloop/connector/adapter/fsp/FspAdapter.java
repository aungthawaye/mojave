package io.mojaloop.connector.adapter.fsp;

import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.spec.core.PartiesTypeIDPutResponse;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import io.mojaloop.fspiop.spec.core.QuotesIDPutResponse;
import io.mojaloop.fspiop.spec.core.QuotesPostRequest;
import io.mojaloop.fspiop.spec.core.TransfersIDPatchResponse;
import io.mojaloop.fspiop.spec.core.TransfersIDPutResponse;
import io.mojaloop.fspiop.spec.core.TransfersPostRequest;

public interface FspAdapter {

    void confirmTransfer(TransfersIDPatchResponse response);

    PartiesTypeIDPutResponse getParties(PartyIdType partyIdType, String partyId, String subId) throws FspiopException;

    TransfersIDPutResponse initiateTransfer(TransfersPostRequest request) throws FspiopException;

    QuotesIDPutResponse quote(QuotesPostRequest request) throws FspiopException;

}
