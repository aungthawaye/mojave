package io.mojaloop.connector.gateway.outbound.command;

import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Destination;
import io.mojaloop.fspiop.spec.core.QuotesIDPutResponse;
import io.mojaloop.fspiop.spec.core.QuotesPostRequest;

public interface RequestQuotesCommand {

    Output execute(Input input) throws FspiopException;

    record Input(Destination destination, QuotesPostRequest request) {}

    record Output(QuotesIDPutResponse response) {}
}
