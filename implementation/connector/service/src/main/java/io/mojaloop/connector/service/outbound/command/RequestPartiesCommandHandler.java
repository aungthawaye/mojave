package io.mojaloop.connector.service.outbound.command;

import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.connector.service.outbound.ConnectorOutboundConfiguration;
import io.mojaloop.fspiop.common.error.ErrorDefinition;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.invoker.api.parties.GetParties;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import io.mojaloop.fspiop.spec.core.PartiesTypeIDPutResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
class RequestPartiesCommandHandler implements RequestPartiesCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestPartiesCommandHandler.class.getName());

    private final GetParties getParties;

    private final PubSubClient pubSubClient;

    private final ConnectorOutboundConfiguration.OutboundSettings outboundSettings;

    public RequestPartiesCommandHandler(GetParties getParties,
                                        PubSubClient pubSubClient,
                                        ConnectorOutboundConfiguration.OutboundSettings outboundSettings) {

        assert null != getParties;
        assert null != pubSubClient;
        assert null != outboundSettings;

        this.getParties = getParties;
        this.pubSubClient = pubSubClient;
        this.outboundSettings = outboundSettings;
    }

    @Override
    public Output execute(Input input) throws FspiopException {

        LOGGER.info("Invoking GetParties : {}", input);

        assert input != null;

        var withSubId = input.subId() != null && !input.subId().isBlank();
        var subIdOrNot = withSubId ? "/" + input.subId() : "";
        var resultTopic = "lookup:" + input.partyIdType() + "/" + input.partyId() + subIdOrNot;
        var errorTopic = "lookup-error:" + input.partyIdType() + "/" + input.partyId() + subIdOrNot;

        // Listening to the pub/sub
        var blocker = new CountDownLatch(1);

        AtomicReference<PartiesTypeIDPutResponse> responseRef = new AtomicReference<>();
        AtomicReference<ErrorInformationObject> errorRef = new AtomicReference<>();

        var resultSubscription = this.pubSubClient.subscribe(resultTopic, new PubSubClient.MessageHandler() {

            @Override
            public void handle(String channel, Object message) {

                if (message instanceof PartiesTypeIDPutResponse response) {
                    responseRef.set(response);
                }

                blocker.countDown();
            }

            @Override
            public Class<PartiesTypeIDPutResponse> messageType() {

                return PartiesTypeIDPutResponse.class;
            }
        }, this.outboundSettings.pubSubTimeout());

        var errorSubscription = this.pubSubClient.subscribe(errorTopic, new PubSubClient.MessageHandler() {

            @Override
            public void handle(String channel, Object message) {

                if (message instanceof ErrorInformationObject response) {
                    errorRef.set(response);
                }

                blocker.countDown();
            }

            @Override
            public Class<?> messageType() {

                return ErrorInformationObject.class;
            }
        }, this.outboundSettings.pubSubTimeout());

        if (withSubId) {
            this.getParties.getParties(input.destination(), input.partyIdType(), input.partyId(), input.subId());
        } else {
            this.getParties.getParties(input.destination(), input.partyIdType(), input.partyId());
        }

        try {

            var ok = blocker.await(this.outboundSettings.putResultTimeout(), TimeUnit.MILLISECONDS);

            if (!ok) {
                throw new FspiopException(FspiopErrors.SERVER_TIMED_OUT, "Timed out while waiting for response from the Hub.");
            }

        } catch (InterruptedException ignored) { } finally {
            this.pubSubClient.unsubscribe(resultSubscription);
            this.pubSubClient.unsubscribe(errorSubscription);
        }

        if (responseRef.get() != null) {
            return new Output(responseRef.get());
        }

        var error = errorRef.get();
        var errorDefinition = FspiopErrors.find(error.getErrorInformation().getErrorCode());

        throw new FspiopException(new ErrorDefinition(errorDefinition.code(),
                                                      errorDefinition.name(),
                                                      error.getErrorInformation().getErrorDescription()));

    }

}
