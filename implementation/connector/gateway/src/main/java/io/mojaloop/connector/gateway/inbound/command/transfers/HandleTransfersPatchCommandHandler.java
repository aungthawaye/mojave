package io.mojaloop.connector.gateway.inbound.command.transfers;

import io.mojaloop.connector.adapter.fsp.FspCoreAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HandleTransfersPatchCommandHandler implements HandleTransfersPatchCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandleTransfersPatchCommandHandler.class.getName());

    private final FspCoreAdapter fspCoreAdapter;

    public HandleTransfersPatchCommandHandler(FspCoreAdapter fspCoreAdapter) {

        assert null != fspCoreAdapter;

        this.fspCoreAdapter = fspCoreAdapter;
    }

    @Override
    public HandleTransfersPatchCommand.Output execute(HandleTransfersPatchCommand.Input input) {

        try {

            LOGGER.info("Calling FSP adapter to initiate transfer for : {}", input);
            this.fspCoreAdapter.patchTransfers(input.source(), input.transferId(), input.response());
            LOGGER.info("Done calling FSP adapter to initiate transfer for : {}", input);

        } catch (Exception e) {

            LOGGER.error("Error handling PatchTransfers", e);
        }

        return null;
    }

}
