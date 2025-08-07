package io.mojaloop.connector.service.inbound.command.quotes;

import io.mojaloop.connector.adapter.fsp.FspAdapter;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Destination;
import io.mojaloop.fspiop.invoker.api.quotes.PutQuotes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HandleQuotesRequestCommandHandler implements HandleQuotesRequestCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandleQuotesRequestCommandHandler.class.getName());

    private final FspAdapter fspAdapter;

    private final PutQuotes putQuotes;

    public HandleQuotesRequestCommandHandler(FspAdapter fspAdapter, PutQuotes putQuotes) {

        assert null != fspAdapter;
        assert null != putQuotes;

        this.fspAdapter = fspAdapter;
        this.putQuotes = putQuotes;
    }

    @Override
    public void execute(Input input) throws FspiopException {

        var destination = new Destination(input.source().sourceFspCode());

        try {

            LOGGER.info("Calling FSP adapter to get quote for : {}", input);
            var response = this.fspAdapter.quote(input.request());
            LOGGER.info("FSP adapter returned quote : {}", response);

            LOGGER.info("Responding the result to Hub : {}", response);
            this.putQuotes.putQuotes(destination, input.quoteId(), response);
            LOGGER.info("Responded the result to Hub");

        } catch (FspiopException e) {

            this.putQuotes.putQuotesError(destination, input.quoteId(), e.toErrorObject());
        }
    }

}