package io.mojaloop.connector.gateway.outbound.command;

import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.connector.gateway.outbound.ConnectorOutboundConfiguration;
import io.mojaloop.fspiop.common.error.ErrorDefinition;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.invoker.api.quotes.PostQuotes;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import io.mojaloop.fspiop.spec.core.QuotesIDPutResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
class RequestQuotesCommandHandler implements RequestQuotesCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestQuotesCommandHandler.class.getName());

    private final PostQuotes postQuotes;

    private final PubSubClient pubSubClient;

    private final ConnectorOutboundConfiguration.OutboundSettings outboundSettings;

    public RequestQuotesCommandHandler(PostQuotes postQuotes,
                                       PubSubClient pubSubClient,
                                       ConnectorOutboundConfiguration.OutboundSettings outboundSettings) {

        assert null != postQuotes;
        assert null != pubSubClient;
        assert null != outboundSettings;

        this.postQuotes = postQuotes;
        this.pubSubClient = pubSubClient;
        this.outboundSettings = outboundSettings;
    }

    @Override
    public Output execute(Input input) throws FspiopException {

        LOGGER.info("Invoking PostQuotes : {}", input);

        assert input != null;
        assert input.request() != null;

        var quoteId = input.request().getQuoteId();
        var resultTopic = "quotes:" + quoteId;
        var errorTopic = "quotes-error:" + quoteId;

        // Listening to the pub/sub
        var blocker = new CountDownLatch(1);

        AtomicReference<QuotesIDPutResponse> responseRef = new AtomicReference<>();
        AtomicReference<ErrorInformationObject> errorRef = new AtomicReference<>();

        var resultSubscription = this.pubSubClient.subscribe(resultTopic, new PubSubClient.MessageHandler() {

            @Override
            public void handle(String channel, Object message) {

                if (message instanceof QuotesIDPutResponse response) {
                    responseRef.set(response);
                }

                blocker.countDown();
            }

            @Override
            public Class<QuotesIDPutResponse> messageType() {

                return QuotesIDPutResponse.class;
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
        }, 60_000);

        this.postQuotes.postQuotes(input.destination(), input.request());

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

        throw new FspiopException(new ErrorDefinition(errorDefinition.errorType(),
                                                      error.getErrorInformation().getErrorDescription()));

    }

}