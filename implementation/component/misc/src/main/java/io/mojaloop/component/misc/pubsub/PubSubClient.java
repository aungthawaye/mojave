package io.mojaloop.component.misc.pubsub;

public interface PubSubClient {

    void closeChannel(String channel);

    void publish(String channel, Object message);

    Subscription subscribe(String channel, MessageHandler handler, int timeout);

    void unsubscribe(Subscription subscription);

    interface MessageHandler {

        void handle(String channel, Object message);

        Class<?> messageType();

    }

    record Subscription(String channel, Object ref) { }

}
