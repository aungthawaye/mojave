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

package io.mojaloop.connector.gateway.inbound.command.quotes;

import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.connector.gateway.component.PubSubKeys;
import io.mojaloop.connector.gateway.data.QuotesResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
class HandlePutQuotesResponseCommandHandler implements HandlePutQuotesResponseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandlePutQuotesResponseCommandHandler.class.getName());

    private final PubSubClient pubSubClient;

    public HandlePutQuotesResponseCommandHandler(PubSubClient pubSubClient) {

        assert null != pubSubClient;

        this.pubSubClient = pubSubClient;
    }

    @Override
    public Output execute(Input input) {

        var channel = PubSubKeys.forQuotes(input.payee(), input.quoteId());
        LOGGER.info("Publishing quotes result to channel : {}", channel);

        this.pubSubClient.publish(channel, new QuotesResult(input.quoteId(), input.response()));
        LOGGER.info("Published quotes result to channel : {}", channel);

        return new Output();
    }

}
