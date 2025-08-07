package io.mojaloop.component.misc.pubsub.local;

import io.mojaloop.component.misc.pubsub.PubSubClient;
import reactor.core.Disposable;

public class LocalPubSubClient implements PubSubClient {

    private final LocalPubSub pubSub;

    public LocalPubSubClient(LocalPubSub pubSub) {

        assert pubSub != null;

        this.pubSub = pubSub;
    }

    @Override
    public void closeChannel(String channel) {

        this.pubSub.closeChannel(channel);
    }

    @Override
    public void publish(String channel, Object message) {

        this.pubSub.publish(channel, message);
    }

    @Override
    public Subscription subscribe(String channel, PubSubClient.MessageHandler handler, int timeout) {

        var disposable = this.pubSub.subscribe(channel, message -> handler.handle(channel, message), timeout);

        return new Subscription(channel, disposable);
    }

    @Override
    public void unsubscribe(Subscription subscription) {

        this.pubSub.unsubscribe(subscription.channel(), (Disposable) subscription.ref());
    }

}
