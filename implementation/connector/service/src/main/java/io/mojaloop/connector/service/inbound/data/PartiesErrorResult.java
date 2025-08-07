package io.mojaloop.connector.service.inbound.data;

import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import io.mojaloop.fspiop.spec.core.PartyIdType;

public record PartiesErrorResult(PartyIdType partyIdType, String partyId, String subId, ErrorInformationObject errorInformation) { }