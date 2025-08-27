package io.mojaloop.connector.gateway.inbound.data;

import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import io.mojaloop.fspiop.spec.core.PartyIdType;

public record PartiesErrorResult(PartyIdType partyIdType, String partyId, String subId, ErrorInformationObject errorInformation) { }