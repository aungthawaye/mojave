package io.mojaloop.connector.service.inbound.data;

import io.mojaloop.fspiop.spec.core.ErrorInformationObject;

public record TransfersErrorResult(String transferId, ErrorInformationObject errorInformation) { }