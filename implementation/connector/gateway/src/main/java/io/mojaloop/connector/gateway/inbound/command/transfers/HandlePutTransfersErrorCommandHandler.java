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

package io.mojaloop.connector.gateway.inbound.command.transfers;

import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.connector.gateway.component.PubSubKeys;
import io.mojaloop.connector.gateway.data.TransfersErrorResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
class HandlePutTransfersErrorCommandHandler implements HandlePutTransfersErrorCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        HandlePutTransfersErrorCommandHandler.class.getName());

    private final PubSubClient pubSubClient;

    public HandlePutTransfersErrorCommandHandler(PubSubClient pubSubClient) {

        assert null != pubSubClient;

        this.pubSubClient = pubSubClient;
    }

    @Override
    public Output execute(Input input) {

        var channel = PubSubKeys.forTransfersError(input.transferId());
        LOGGER.info("Publishing transfers error result to channel : {}", channel);

        this.pubSubClient.publish(
            channel, new TransfersErrorResult(input.transferId(), input.errorInformationObject()));
        LOGGER.info("Published transfers error result to channel : {}", channel);

        return new Output();
    }

}
