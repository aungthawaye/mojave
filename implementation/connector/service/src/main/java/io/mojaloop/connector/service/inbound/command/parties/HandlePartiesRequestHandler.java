package io.mojaloop.connector.service.inbound.command.parties;

import io.mojaloop.connector.adapter.fsp.FspAdapter;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Destination;
import io.mojaloop.fspiop.invoker.api.parties.PutParties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HandlePartiesRequestHandler implements HandlePartiesRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandlePartiesRequestHandler.class.getName());

    private final FspAdapter fspAdapter;

    private final PutParties putParties;

    public HandlePartiesRequestHandler(FspAdapter fspAdapter, PutParties putParties) {

        assert null != fspAdapter;
        assert null != putParties;

        this.fspAdapter = fspAdapter;
        this.putParties = putParties;
    }

    @Override
    public void execute(Input input) throws FspiopException {

        var destination = new Destination(input.source().sourceFspCode());
        var hasSubId = input.subId() != null;

        try {

            LOGGER.info("Calling FSP adapter to get parties for : {}", input);
            var response = this.fspAdapter.getParties(input.partyIdType(), input.partyId(), input.subId());
            LOGGER.info("FSP adapter returned parties : {}", response);

            if (hasSubId) {
                LOGGER.info("Responding the result to Hub : {}", response);
                this.putParties.putParties(destination, input.partyIdType(), input.partyId(), input.subId(), response);
                LOGGER.info("Responded the result to Hub");
            } else {
                LOGGER.info("Responding the result to Hub : {}", response);
                this.putParties.putParties(destination, input.partyIdType(), input.partyId(), response);
                LOGGER.info("Responded the result to Hub");
            }

        } catch (FspiopException e) {

            if (hasSubId) {
                this.putParties.putPartiesError(destination, input.partyIdType(), input.partyId(), input.subId(), e.toErrorObject());
            } else {
                this.putParties.putPartiesError(destination, input.partyIdType(), input.partyId(), e.toErrorObject());
            }
        }

    }

}
