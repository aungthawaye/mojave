package io.mojaloop.connector.service.inbound.command.transfers;

import io.mojaloop.fspiop.common.type.Source;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;

public interface HandleTransfersErrorCommand {

    Output execute(Input input);

    record Input(Source source, String transferId, ErrorInformationObject errorInformationObject) { }

    record Output() { }

}
