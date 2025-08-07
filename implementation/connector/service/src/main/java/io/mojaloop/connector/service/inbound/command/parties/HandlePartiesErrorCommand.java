package io.mojaloop.connector.service.inbound.command.parties;

import io.mojaloop.fspiop.common.type.Source;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import io.mojaloop.fspiop.spec.core.PartyIdType;

public interface HandlePartiesErrorCommand {

    Output execute(Input input);

    record Input(Source source, PartyIdType partyIdType, String partyId, String subId, ErrorInformationObject errorInformationObject) { }

    record Output() { }

}
