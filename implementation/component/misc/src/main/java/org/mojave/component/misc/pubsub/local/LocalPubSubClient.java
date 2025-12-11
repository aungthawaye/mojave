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

import org.mojave.component.misc.pubsub.PubSubClient;
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
    public Subscription subscribe(String channel,
                                  PubSubClient.MessageHandler handler,
                                  int timeout) {

        var disposable = this.pubSub.subscribe(
            channel, message -> handler.handle(channel, message), timeout);

        return new Subscription(channel, disposable);
    }

    @Override
    public void unsubscribe(Subscription subscription) {

        this.pubSub.unsubscribe(subscription.channel(), (Disposable) subscription.ref());
    }

}
