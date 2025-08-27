package io.mojaloop.core.participant.contract.command.fsp;

import io.mojaloop.core.common.datatype.enumeration.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.participant.contract.exception.FspIdNotFoundException;

public interface DeactivateEndpointCommand {

    Output execute(Input input) throws FspIdNotFoundException;

    record Input(FspId fspId, EndpointType endpointType) { }

    record Output(boolean deactivated) { }

}
