/*-
 * ================================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
package org.mojave.connector.gateway.outbound.component;

import org.mojave.component.misc.pubsub.PubSubClient;
import org.mojave.connector.gateway.outbound.ConnectorOutboundConfiguration;
import org.mojave.fspiop.common.error.FspiopErrors;
import org.mojave.fspiop.common.exception.FspiopException;
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

    private PubSubClient.Subscription hubErrorSubscription;

    public FspiopResultListener(PubSubClient pubSubClient,
                                ConnectorOutboundConfiguration.OutboundSettings outboundSettings,
                                Class<R> resultClazz,
                                Class<E> errorClazz) {

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
            var ok = this.blocker.await(
                this.outboundSettings.putResultTimeoutMs(), TimeUnit.MILLISECONDS);

            if (!ok) {
                throw new FspiopException(
                    FspiopErrors.SERVER_TIMED_OUT,
                    "Timed out while waiting for response from the Hub.");
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

        this.init(resultTopic, errorTopic, null);
    }

    public void init(String resultTopic, String errorTopic, String hubErrorTopic) {

        LOGGER.debug(
            "Listening for results on channel {} and errors on channel {} and hub channel {}",
            resultTopic, errorTopic, hubErrorTopic);

        this.resultSubscription = this.pubSubClient.subscribe(
            resultTopic, new PubSubClient.MessageHandler() {

                @Override
                public void handle(String channel, Object message) {

                    LOGGER.debug(
                        "Result message from channel : {}, message : {}", channel, message);

                    if (FspiopResultListener.this.resultClazz.isInstance(message)) {
                        FspiopResultListener.this.responseRef.set(
                            FspiopResultListener.this.resultClazz.cast(message));
                    }

                    FspiopResultListener.this.blocker.countDown();
                }

                @Override
                public Class<R> messageType() {

                    return FspiopResultListener.this.resultClazz;
                }

            }, this.outboundSettings.pubSubTimeoutMs());

        this.errorSubscription = this.pubSubClient.subscribe(
            errorTopic, new PubSubClient.MessageHandler() {

                @Override
                public void handle(String channel, Object message) {

                    LOGGER.debug("Error message from channel : {}, message : {}", channel, message);

                    if (FspiopResultListener.this.errorClazz.isInstance(message)) {
                        FspiopResultListener.this.errorRef.set(
                            FspiopResultListener.this.errorClazz.cast(message));
                    }

                    FspiopResultListener.this.blocker.countDown();
                }

                @Override
                public Class<?> messageType() {

                    return FspiopResultListener.this.errorClazz;
                }

            }, this.outboundSettings.pubSubTimeoutMs());

        if (hubErrorTopic != null) {

            this.hubErrorSubscription = this.pubSubClient.subscribe(
                errorTopic, new PubSubClient.MessageHandler() {

                    @Override
                    public void handle(String channel, Object message) {

                        LOGGER.debug(
                            "Error message from hub channel : {}, message : {}", channel, message);

                        if (FspiopResultListener.this.errorClazz.isInstance(message)) {
                            FspiopResultListener.this.errorRef.set(
                                FspiopResultListener.this.errorClazz.cast(message));
                        }

                        FspiopResultListener.this.blocker.countDown();
                    }

                    @Override
                    public Class<?> messageType() {

                        return FspiopResultListener.this.errorClazz;
                    }

                }, this.outboundSettings.pubSubTimeoutMs());
        }
    }

    public void unsubscribe() {

        if (this.resultSubscription != null) {
            this.pubSubClient.unsubscribe(this.resultSubscription);
        }

        if (this.errorSubscription != null) {
            this.pubSubClient.unsubscribe(this.errorSubscription);
        }

        if (this.hubErrorSubscription != null) {
            this.pubSubClient.unsubscribe(this.hubErrorSubscription);
        }
    }

}
