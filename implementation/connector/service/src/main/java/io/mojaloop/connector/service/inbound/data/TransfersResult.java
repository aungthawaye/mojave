package io.mojaloop.connector.service.inbound.data;

import io.mojaloop.fspiop.spec.core.TransfersIDPutResponse;

public record TransfersResult(String transferId, TransfersIDPutResponse response) { }