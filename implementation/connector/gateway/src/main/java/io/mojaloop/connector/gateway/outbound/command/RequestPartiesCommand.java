package io.mojaloop.connector.gateway.outbound.command;

import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Destination;
import io.mojaloop.fspiop.spec.core.PartiesTypeIDPutResponse;
import io.mojaloop.fspiop.spec.core.PartyIdType;

public interface RequestPartiesCommand {

    Output execute(Input input) throws FspiopException;

    record Input(Destination destination, PartyIdType partyIdType, String partyId, String subId){}

    record Output(PartiesTypeIDPutResponse response){}
}
