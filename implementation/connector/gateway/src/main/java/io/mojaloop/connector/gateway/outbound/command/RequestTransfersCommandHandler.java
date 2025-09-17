package io.mojaloop.connector.gateway.outbound.command;

import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.connector.gateway.inbound.data.TransfersErrorResult;
import io.mojaloop.connector.gateway.inbound.data.TransfersResult;
import io.mojaloop.connector.gateway.outbound.ConnectorOutboundConfiguration;
import io.mojaloop.fspiop.common.error.ErrorDefinition;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.invoker.api.transfers.PostTransfers;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import io.mojaloop.fspiop.spec.core.TransfersIDPutResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
class RequestTransfersCommandHandler implements RequestTransfersCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestTransfersCommandHandler.class.getName());

    private final PostTransfers postTransfers;

    private final PubSubClient pubSubClient;

    private final ConnectorOutboundConfiguration.OutboundSettings outboundSettings;

    public RequestTransfersCommandHandler(PostTransfers postTransfers,
                                          PubSubClient pubSubClient,
                                          ConnectorOutboundConfiguration.OutboundSettings outboundSettings) {

        assert null != postTransfers;
        assert null != pubSubClient;
        assert null != outboundSettings;

        this.postTransfers = postTransfers;
        this.pubSubClient = pubSubClient;
        this.outboundSettings = outboundSettings;
    }

    @Override
    public Output execute(Input input) throws FspiopException {

        LOGGER.info("Invoking PostTransfers : {}", input);

        assert input != null;
        assert input.request() != null;

        var transferId = input.request().getTransferId();
        var resultTopic = "transfers:" + transferId;
        var errorTopic = "transfers-error:" + transferId;

        // Listening to the pub/sub
        var blocker = new CountDownLatch(1);

        AtomicReference<TransfersResult> responseRef = new AtomicReference<>();
        AtomicReference<TransfersErrorResult> errorRef = new AtomicReference<>();

        var resultSubscription = this.pubSubClient.subscribe(resultTopic, new PubSubClient.MessageHandler() {

            @Override
            public void handle(String channel, Object message) {

                if (message instanceof TransfersResult response) {
                    responseRef.set(response);
                }

                blocker.countDown();
            }

            @Override
            public Class<TransfersResult> messageType() {

                return TransfersResult.class;
            }
        }, this.outboundSettings.pubSubTimeout());

        var errorSubscription = this.pubSubClient.subscribe(errorTopic, new PubSubClient.MessageHandler() {

            @Override
            public void handle(String channel, Object message) {

                if (message instanceof TransfersErrorResult response) {
                    errorRef.set(response);
                }

                blocker.countDown();
            }

            @Override
            public Class<?> messageType() {

                return ErrorInformationObject.class;
            }
        }, 60_000);

        this.postTransfers.postTransfers(input.destination(), input.request());

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
        var errorDefinition = FspiopErrors.find(error.errorInformation().getErrorInformation().getErrorCode());

        throw new FspiopException(new ErrorDefinition(errorDefinition.errorType(),
                                                      error.errorInformation().getErrorInformation().getErrorDescription()));

    }

}