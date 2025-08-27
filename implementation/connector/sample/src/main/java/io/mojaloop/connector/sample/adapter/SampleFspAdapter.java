package io.mojaloop.connector.sample.adapter;

import io.mojaloop.connector.adapter.fsp.FspAdapter;
import io.mojaloop.fspiop.spec.core.PartiesTypeIDPutResponse;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import io.mojaloop.fspiop.spec.core.QuotesIDPutResponse;
import io.mojaloop.fspiop.spec.core.QuotesPostRequest;
import io.mojaloop.fspiop.spec.core.TransfersIDPatchResponse;
import io.mojaloop.fspiop.spec.core.TransfersIDPutResponse;
import io.mojaloop.fspiop.spec.core.TransfersPostRequest;

public class SampleFspAdapter implements FspAdapter {

    @Override
    public void confirmTransfer(TransfersIDPatchResponse response) {

    }

    @Override
    public PartiesTypeIDPutResponse getParties(PartyIdType partyIdType, String partyId, String subId) {

        return null;
    }

    @Override
    public TransfersIDPutResponse initiateTransfer(TransfersPostRequest request) {

        return null;
    }

    @Override
    public QuotesIDPutResponse quote(QuotesPostRequest request) {

        return null;
    }

}
