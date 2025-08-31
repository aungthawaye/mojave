package io.mojaloop.connector.adapter.fsp;

import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Source;
import io.mojaloop.fspiop.spec.core.PartiesTypeIDPutResponse;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import io.mojaloop.fspiop.spec.core.QuotesIDPutResponse;
import io.mojaloop.fspiop.spec.core.QuotesPostRequest;
import io.mojaloop.fspiop.spec.core.TransfersIDPatchResponse;
import io.mojaloop.fspiop.spec.core.TransfersIDPutResponse;
import io.mojaloop.fspiop.spec.core.TransfersPostRequest;

public interface FspAdapter {

    void confirmTransfer(Source source, TransfersIDPatchResponse response);

    PartiesTypeIDPutResponse getParties(Source source, PartyIdType partyIdType, String partyId, String subId) throws FspiopException;

    TransfersIDPutResponse initiateTransfer(Source source, TransfersPostRequest request) throws FspiopException;

    QuotesIDPutResponse quote(Source source, QuotesPostRequest request) throws FspiopException;

}
