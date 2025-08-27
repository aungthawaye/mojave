package io.mojaloop.connector.gateway.inbound.command.parties;

import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.connector.gateway.inbound.data.PartiesErrorResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
class HandlePartiesErrorCommandHandler implements HandlePartiesErrorCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandlePartiesErrorCommandHandler.class.getName());

    private final PubSubClient pubSubClient;

    public HandlePartiesErrorCommandHandler(PubSubClient pubSubClient) {

        assert null != pubSubClient;

        this.pubSubClient = pubSubClient;
    }

    @Override
    public Output execute(Input input) {

        var withSubId = input.subId() != null && !input.subId().isBlank();
        var channel =
            "parties-error:" + input.partyIdType().name() + "/" + input.partyId() + (withSubId ? "/" + input.subId() : "") + "/error";
        LOGGER.info("Publishing parties error result to channel : {}", channel);

        this.pubSubClient.publish(channel,
                                  new PartiesErrorResult(input.partyIdType(),
                                                         input.partyId(),
                                                         input.subId(),
                                                         input.errorInformationObject()));
        LOGGER.info("Published parties error result to channel : {}", channel);

        return new Output();
    }

}