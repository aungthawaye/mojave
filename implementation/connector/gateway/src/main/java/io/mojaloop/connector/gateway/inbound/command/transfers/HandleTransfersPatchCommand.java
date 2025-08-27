package io.mojaloop.connector.gateway.inbound.command.transfers;

import io.mojaloop.fspiop.common.type.Source;
import io.mojaloop.fspiop.spec.core.TransfersIDPatchResponse;

public interface HandleTransfersPatchCommand {

    Output execute(Input input);

    record Input(Source source, String transferId, TransfersIDPatchResponse response) { }

    record Output() { }

}
