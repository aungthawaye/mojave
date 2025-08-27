package io.mojaloop.connector.gateway.inbound.data;

import io.mojaloop.fspiop.spec.core.ErrorInformationObject;

public record TransfersErrorResult(String transferId, ErrorInformationObject errorInformation) { }