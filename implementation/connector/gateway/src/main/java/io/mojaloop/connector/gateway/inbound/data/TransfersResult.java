package io.mojaloop.connector.gateway.inbound.data;

import io.mojaloop.fspiop.spec.core.TransfersIDPutResponse;

public record TransfersResult(String transferId, TransfersIDPutResponse response) { }