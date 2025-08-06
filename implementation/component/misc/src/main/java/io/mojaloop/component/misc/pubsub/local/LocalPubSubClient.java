package io.mojaloop.component.misc.pubsub.local;

import io.mojaloop.component.misc.pubsub.PubSubClient;

public class LocalPubSubClient implements PubSubClient {

    private final LocalPubSub pubSub;

    public LocalPubSubClient(LocalPubSub pubSub) {

        assert pubSub != null;

        this.pubSub = pubSub;
    }

    @Override
    public void close(String channel) {

        this.pubSub.closeChannel(channel);
    }

    @Override
    public void publish(String channel, Object message) {

        this.pubSub.publish(channel, message);
    }

    @Override
    public void subscribe(String channel, io.mojaloop.component.misc.pubsub.PubSubClient.MessageHandler handler) {

        this.pubSub.subscribe(channel, message -> handler.handle(channel, message));
    }

}
