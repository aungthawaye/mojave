package io.mojaloop.core.lookup.domain.command;

import io.mojaloop.common.fspiop.support.Destination;

public interface LookUpInPayee {

    Output execute(Input input);

    record Input(Destination destination, String partyIdType, String partyId, String partySubId) { }

    record Output() { }

}
