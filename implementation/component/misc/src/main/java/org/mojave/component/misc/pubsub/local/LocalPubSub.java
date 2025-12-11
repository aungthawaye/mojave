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
package org.mojave.component.misc.pubsub.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class LocalPubSub {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalPubSub.class);

    private final ConcurrentHashMap<String, Channel> channels = new ConcurrentHashMap<>();

    /**
     * Close the channel and unsubscribe all subscribers.
     */
    public void closeChannel(String channelName) {

        Channel channel = this.channels.remove(channelName);

        if (channel != null) {
            channel.subscriptions.forEach(Disposable::dispose);
            channel.sink.tryEmitComplete();
            LOGGER.debug("Closed channel {}", channelName);
        }
    }

    /**
     * Publish a message to a channel.
     */
    public void publish(String channelName, Object message) {

        Channel channel = this.channels.computeIfAbsent(
            channelName, ch -> new Channel(Sinks.many().multicast().onBackpressureBuffer()));
        Sinks.EmitResult result = channel.sink.tryEmitNext(message);

        if (result.isFailure()) {
            LOGGER.error("Failed to publish message to channel {}", channelName);
        } else {
            LOGGER.debug("Published message to channel {}", channelName);
        }
    }

    /**
     * Shutdown all channels and subscribers.
     */
    public void shutdown() {

        for (Map.Entry<String, Channel> entry : channels.entrySet()) {
            entry.getValue().subscriptions.forEach(Disposable::dispose);
            entry.getValue().sink.tryEmitComplete();
        }

        this.channels.clear();
        LOGGER.info("All channels shut down");
    }

    /**
     * Subscribe to a channel. Automatically cleans up if no more subscribers remain.
     */
    public Disposable subscribe(String channelName, Consumer<Object> handler, long timeout) {

        Channel channel = channels.computeIfAbsent(
            channelName, ch -> new Channel(Sinks.many().multicast().onBackpressureBuffer()));

        // Use take(timeout) to automatically unsubscribe after timeout
        Flux<Object> flux = channel.sink.asFlux().take(timeout);

        AtomicReference<Disposable> ref = new AtomicReference<>();

        Disposable disposable = flux.subscribe(
            handler, error -> LOGGER.error("Error in subscriber of channel {}", channelName, error),
            () -> {

                LOGGER.debug("Subscriber timeout/completed on channel {}", channelName);

                Disposable removing = ref.get();

                channel.subscriptions.remove(removing);

                if (channel.subscriptions.isEmpty()) {

                    LOGGER.debug(
                        "No more subscribers on channel {}, removing channel", channelName);

                    channel.sink.tryEmitComplete();

                    channels.remove(channelName);
                }
            });

        ref.set(disposable);

        channel.subscriptions.add(disposable);

        return disposable;
    }

    /**
     * Unsubscribe a specific subscription from a channel.
     */
    public void unsubscribe(String channelName, Disposable subscription) {

        Channel channel = this.channels.get(channelName);

        if (channel != null && subscription != null) {

            subscription.dispose();

            channel.subscriptions.remove(subscription);

            LOGGER.debug("Unsubscribed from channel {}", channelName);

            // Clean up channel if no more subscribers
            if (channel.subscriptions.isEmpty()) {

                LOGGER.debug("No more subscribers on channel {}, removing channel", channelName);

                channel.sink.tryEmitComplete();

                this.channels.remove(channelName);
            }
        }
    }

    private static class Channel {

        final Sinks.Many<Object> sink;

        final Set<Disposable> subscriptions = new CopyOnWriteArraySet<>();

        Channel(Sinks.Many<Object> sink) {

            this.sink = sink;
        }

    }

}
