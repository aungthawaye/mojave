package io.mojaloop.common.centralcache.data;

import io.mojaloop.common.fspiop.model.core.PartyIdType;

public record OracleData(PartyIdType partyIdType, String currency, String endpointUrl, String endpointType, boolean isDefault) { }
