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

package io.mojaloop.connector.gateway.inbound.command.parties;

import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.connector.gateway.component.PubSubKeys;
import io.mojaloop.connector.gateway.data.PartiesErrorResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
class HandlePartiesErrorCommandHandler implements HandlePartiesErrorCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandlePartiesErrorCommandHandler.class.getName());

    private final PubSubClient pubSubClient;

    public HandlePartiesErrorCommandHandler(PubSubClient pubSubClient) {

        assert null != pubSubClient;

        this.pubSubClient = pubSubClient;
    }

    @Override
    public Output execute(Input input) {

        var channel = PubSubKeys.forPartiesError(input.source().sourceFspCode(), input.partyIdType(), input.partyId(), input.subId());
        LOGGER.info("Publishing parties error result to channel : {}", channel);

        this.pubSubClient.publish(channel, new PartiesErrorResult(input.partyIdType(), input.partyId(), input.subId(), input.errorInformationObject()));
        LOGGER.info("Published parties error result to channel : {}", channel);

        return new Output();
    }

}
