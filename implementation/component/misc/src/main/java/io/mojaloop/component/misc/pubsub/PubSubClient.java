package io.mojaloop.component.misc.pubsub;

public interface PubSubClient {

    void close(String channel);

    void publish(String channel, Object message);

    void subscribe(String channel, MessageHandler handler);

    interface MessageHandler {

        void handle(String channel, Object message);

        Class<?> messageType();

    }

}
