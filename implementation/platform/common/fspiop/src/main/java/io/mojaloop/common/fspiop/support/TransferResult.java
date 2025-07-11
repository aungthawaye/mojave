package io.mojaloop.common.fspiop.support;

import io.mojaloop.common.fspiop.model.core.TransfersIDPutResponse;

public record TransferResult(String transferId, TransfersIDPutResponse details) { }
