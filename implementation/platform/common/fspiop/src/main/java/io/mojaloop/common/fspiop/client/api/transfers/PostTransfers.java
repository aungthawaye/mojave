package io.mojaloop.common.fspiop.client.api.transfers;

import io.mojaloop.common.fspiop.core.model.TransfersPostRequest;
import io.mojaloop.common.fspiop.support.Destination;

public interface PostTransfers {

    void postTransfers(Destination destination, TransfersPostRequest transfersPostRequest);

}
