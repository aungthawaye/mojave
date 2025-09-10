package io.mojaloop.connector.gateway.inbound.command.parties;

import io.mojaloop.connector.adapter.fsp.FspCoreAdapter;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Destination;
import io.mojaloop.fspiop.invoker.api.parties.PutParties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
class HandlePartiesRequestCommandHandler implements HandlePartiesRequestCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandlePartiesRequestCommandHandler.class.getName());

    private final FspCoreAdapter fspCoreAdapter;

    private final PutParties putParties;

    public HandlePartiesRequestCommandHandler(FspCoreAdapter fspCoreAdapter, PutParties putParties) {

        assert fspCoreAdapter != null;
        assert putParties != null;

        this.fspCoreAdapter = fspCoreAdapter;
        this.putParties = putParties;
    }

    @Override
    public void execute(Input input) throws FspiopException {

        var destination = new Destination(input.source().sourceFspCode());
        var hasSubId = input.subId() != null;

        try {

            LOGGER.info("Calling FSP adapter to get parties for : {}", input);
            var response = this.fspCoreAdapter.getParties(input.source(), input.partyIdType(), input.partyId(), input.subId());
            LOGGER.info("FSP adapter returned parties : {}", response);

            LOGGER.info("Responding the result to Hub : {}", response);
            if (hasSubId) {
                this.putParties.putParties(destination, input.partyIdType(), input.partyId(), input.subId(), response);
            } else {
                this.putParties.putParties(destination, input.partyIdType(), input.partyId(), response);
            }
            LOGGER.info("Responded the result to Hub");

        } catch (FspiopException e) {

            if (hasSubId) {
                this.putParties.putPartiesError(destination, input.partyIdType(), input.partyId(), input.subId(), e.toErrorObject());
            } else {
                this.putParties.putPartiesError(destination, input.partyIdType(), input.partyId(), e.toErrorObject());
            }
        }

    }

}
