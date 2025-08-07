package io.mojaloop.connector.service.inbound.command.parties;

import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Source;
import io.mojaloop.fspiop.spec.core.PartyIdType;

public interface HandlePartiesRequestCommand {

    void execute(Input input) throws FspiopException;

    record Input(Source source, PartyIdType partyIdType, String partyId, String subId) { }

}
