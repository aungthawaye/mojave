package io.mojaloop.connector.gateway.outbound.component;

import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.connector.gateway.outbound.ConnectorOutboundConfiguration;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class FspiopResultListener<R, E> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspiopResultListener.class);

    private final PubSubClient pubSubClient;

    private final ConnectorOutboundConfiguration.OutboundSettings outboundSettings;

    private final Class<R> resultClazz;

    private final Class<E> errorClazz;

    private final AtomicReference<R> responseRef = new AtomicReference<>();

    private final AtomicReference<E> errorRef = new AtomicReference<>();

    private final CountDownLatch blocker = new CountDownLatch(1);

    private PubSubClient.Subscription errorSubscription;

    private PubSubClient.Subscription resultSubscription;

    public FspiopResultListener(PubSubClient pubSubClient, ConnectorOutboundConfiguration.OutboundSettings outboundSettings, Class<R> resultClazz, Class<E> errorClazz) {

        assert pubSubClient != null;
        assert outboundSettings != null;
        assert resultClazz != null;
        assert errorClazz != null;

        this.pubSubClient = pubSubClient;
        this.outboundSettings = outboundSettings;
        this.resultClazz = resultClazz;
        this.errorClazz = errorClazz;
    }

    public void await() throws FspiopException {

        try {
            var ok = this.blocker.await(this.outboundSettings.putResultTimeoutMs(), TimeUnit.MILLISECONDS);

            if (!ok) {
                throw new FspiopException(FspiopErrors.SERVER_TIMED_OUT, "Timed out while waiting for response from the Hub.");
            }

        } catch (InterruptedException ignored) { } finally {
            this.unsubscribe();
        }
    }

    public E getError() {

        return this.errorRef.get();
    }

    public R getResponse() {

        return this.responseRef.get();
    }

    public void init(String resultTopic, String errorTopic) {

        LOGGER.debug("Listening for results on channel {} and errors on channel {}", resultTopic, errorTopic);

        this.resultSubscription = this.pubSubClient.subscribe(resultTopic, new PubSubClient.MessageHandler() {

            @Override
            public void handle(String channel, Object message) {

                LOGGER.debug("Result message from channel : {}, message : {}", channel, message);

                if (FspiopResultListener.this.resultClazz.isInstance(message)) {
                    FspiopResultListener.this.responseRef.set(FspiopResultListener.this.resultClazz.cast(message));
                }

                FspiopResultListener.this.blocker.countDown();
            }

            @Override
            public Class<R> messageType() {

                return FspiopResultListener.this.resultClazz;
            }

        }, this.outboundSettings.pubSubTimeoutMs());

        this.errorSubscription = this.pubSubClient.subscribe(errorTopic, new PubSubClient.MessageHandler() {

            @Override
            public void handle(String channel, Object message) {

                LOGGER.debug("Error message from channel : {}, message : {}", channel, message);

                if (FspiopResultListener.this.errorClazz.isInstance(message)) {
                    FspiopResultListener.this.errorRef.set(FspiopResultListener.this.errorClazz.cast(message));
                }

                FspiopResultListener.this.blocker.countDown();
            }

            @Override
            public Class<?> messageType() {

                return FspiopResultListener.this.errorClazz;
            }

        }, this.outboundSettings.pubSubTimeoutMs());
    }

    public void unsubscribe() {

        if (this.resultSubscription != null) {
            this.pubSubClient.unsubscribe(this.resultSubscription);
        }

        if (this.errorSubscription != null) {
            this.pubSubClient.unsubscribe(this.errorSubscription);
        }
    }

}
