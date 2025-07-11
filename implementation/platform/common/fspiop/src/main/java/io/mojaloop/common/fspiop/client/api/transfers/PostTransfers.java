package io.mojaloop.common.fspiop.client.api.transfers;

import io.mojaloop.common.fspiop.model.core.TransfersPostRequest;
import io.mojaloop.common.fspiop.support.Destination;

public interface PostTransfers {

    void postTransfers(Destination destination, TransfersPostRequest transfersPostRequest);

}
