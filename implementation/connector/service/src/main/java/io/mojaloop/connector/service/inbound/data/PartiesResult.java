package io.mojaloop.connector.service.inbound.data;

import io.mojaloop.fspiop.spec.core.PartiesTypeIDPutResponse;
import io.mojaloop.fspiop.spec.core.PartyIdType;

public record PartiesResult(PartyIdType partyIdType, String partyId, String subId, PartiesTypeIDPutResponse response) { }
