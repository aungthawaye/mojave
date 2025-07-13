package io.mojaloop.core.participant.contract.data;

import io.mojaloop.common.datatype.enumeration.fspiop.EndpointType;
import io.mojaloop.common.datatype.identifier.participant.FspId;
import io.mojaloop.common.datatype.type.fspiop.FspCode;
import io.mojaloop.common.fspiop.model.core.Currency;

import java.util.Map;

public record FspData(FspId fspId,
                      FspCode fspCode,
                      String name,
                      Currency[] supportedCurrencies,
                      Map<EndpointType, EndpointData> endpoints) {

    public record EndpointData(EndpointType endpointType, String endpointUrl) { }

}
