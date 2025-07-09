package io.mojaloop.common.fspiop.support;

import io.mojaloop.common.fspiop.core.model.TransfersIDPutResponse;

public record TransferResult(String transferId, TransfersIDPutResponse details) { }
