package io.mojaloop.connector.gateway.inbound.command.transfers;

import io.mojaloop.connector.adapter.fsp.FspCoreAdapter;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Destination;
import io.mojaloop.fspiop.invoker.api.transfers.PutTransfers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
class HandleTransfersRequestCommandHandler implements HandleTransfersRequestCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandleTransfersRequestCommandHandler.class.getName());

    private final FspCoreAdapter fspCoreAdapter;

    private final PutTransfers putTransfers;

    public HandleTransfersRequestCommandHandler(FspCoreAdapter fspCoreAdapter, PutTransfers putTransfers) {

        assert fspCoreAdapter != null;
        assert putTransfers != null;

        this.fspCoreAdapter = fspCoreAdapter;
        this.putTransfers = putTransfers;
    }

    @Override
    public void execute(Input input) throws FspiopException {

        var destination = new Destination(input.source().sourceFspCode());

        try {

            LOGGER.info("Calling FSP adapter to initiate transfer for : {}", input);
            var response = this.fspCoreAdapter.postTransfers(input.source(), input.request());
            LOGGER.info("FSP adapter returned transfer response : {}", response);

            LOGGER.info("Responding the result to Hub : {}", response);
            this.putTransfers.putTransfers(destination, input.transferId(), response);
            LOGGER.info("Responded the result to Hub");

        } catch (FspiopException e) {

            this.putTransfers.putTransfersError(destination, input.transferId(), e.toErrorObject());
        }
    }

}