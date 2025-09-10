package io.mojaloop.connector.adapter.fsp.client;

import io.mojaloop.connector.adapter.fsp.payload.Parties;
import io.mojaloop.connector.adapter.fsp.payload.Quotes;
import io.mojaloop.connector.adapter.fsp.payload.Transfers;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Source;

public interface FspClient {

    Parties.Get.Response getParties(Source source, Parties.Get.Request request) throws FspiopException;

    void patchTransfers(Source source, Transfers.Patch.Request request) throws FspiopException;

    Quotes.Post.Response postQuotes(Source source, Quotes.Post.Request request) throws FspiopException;

    Transfers.Post.Response postTransfers(Source source, Transfers.Post.Request request) throws FspiopException;

}
