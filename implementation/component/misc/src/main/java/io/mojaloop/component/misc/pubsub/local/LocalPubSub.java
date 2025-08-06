package io.mojaloop.component.misc.pubsub.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class LocalPubSub {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalPubSub.class);

    // Map to hold channel -> Sink
    private final ConcurrentHashMap<String, Sinks.Many<Object>> channels = new ConcurrentHashMap<>();

    /**
     * Unsubscribe all subscribers and remove the channel.
     */
    public void closeChannel(String channel) {

        Sinks.Many<Object> sink = this.channels.remove(channel);

        if (sink != null) {
            sink.tryEmitComplete();
        }
    }

    /**
     * Publish a message to a channel.
     */
    public void publish(String channel, Object message) {

        Sinks.Many<Object> sink = channels.computeIfAbsent(channel, ch -> Sinks.many().multicast().onBackpressureBuffer());

        Sinks.EmitResult result = sink.tryEmitNext(message);

        if (result.isFailure()) {
            LOGGER.error("Failed to publish message to channel {}", channel);
        }else{
            LOGGER.debug("Published message to channel {}", channel);
        }
    }

    /**
     * Clear all channels.
     */
    public void shutdown() {

        channels.forEach((ch, sink) -> sink.tryEmitComplete());
        channels.clear();
    }

    /**
     * Subscribe to a channel.
     */
    public void subscribe(String channel, Consumer<Object> handler) {

        Sinks.Many<Object> sink = channels.computeIfAbsent(channel, ch -> Sinks.many().multicast().onBackpressureBuffer());

        Flux<Object> flux = sink.asFlux();
        flux.subscribe(handler);
    }

}
