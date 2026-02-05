/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */
package org.mojave.connector.gateway.inbound.command.quotes;

import org.mojave.component.misc.pubsub.PubSubClient;
import org.mojave.connector.gateway.component.PubSubKeys;
import org.mojave.connector.gateway.data.QuotesResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
class HandlePutQuotesResponseCommandHandler implements HandlePutQuotesResponseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        HandlePutQuotesResponseCommandHandler.class.getName());

    private final PubSubClient pubSubClient;

    public HandlePutQuotesResponseCommandHandler(PubSubClient pubSubClient) {

        Objects.requireNonNull(pubSubClient);

        this.pubSubClient = pubSubClient;
    }

    @Override
    public Output execute(Input input) {

        MDC.put("REQ_ID", input.quoteId());

        var channel = PubSubKeys.forQuotes(input.quoteId());
        LOGGER.info("Publishing quotes result to channel : {}", channel);

        this.pubSubClient.publish(channel, new QuotesResult(input.quoteId(), input.response()));
        LOGGER.info("Published quotes result to channel : {}", channel);

        MDC.remove("REQ_ID");

        return new Output();
    }

}
