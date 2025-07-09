package io.mojaloop.common.fspiop.client.api.transfers;

import io.mojaloop.common.fspiop.support.Destination;

public interface GetTransfers {

    void getTransfers(Destination destination, String transferId);

}
