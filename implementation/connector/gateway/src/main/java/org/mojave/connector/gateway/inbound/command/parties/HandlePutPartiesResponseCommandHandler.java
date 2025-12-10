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

package org.mojave.connector.gateway.inbound.command.parties;

import org.mojave.component.misc.pubsub.PubSubClient;
import org.mojave.connector.gateway.component.PubSubKeys;
import org.mojave.connector.gateway.data.PartiesResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
class HandlePutPartiesResponseCommandHandler implements HandlePutPartiesResponseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        HandlePutPartiesResponseCommandHandler.class.getName());

    private final PubSubClient pubSubClient;

    public HandlePutPartiesResponseCommandHandler(PubSubClient pubSubClient) {

        assert null != pubSubClient;

        this.pubSubClient = pubSubClient;
    }

    @Override
    public Output execute(Input input) {

        var channel = PubSubKeys.forParties(
            input.payee(), input.partyIdType(), input.partyId(), input.subId());

        var result = new PartiesResult(
            input.partyIdType(), input.partyId(), input.subId(), input.response());
        this.pubSubClient.publish(channel, result);

        return new Output();
    }

}
