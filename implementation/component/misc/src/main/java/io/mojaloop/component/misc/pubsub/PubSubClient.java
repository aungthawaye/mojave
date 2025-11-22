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
