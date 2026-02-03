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
package org.mojave.connector.gateway.inbound.command.parties;

import org.mojave.component.misc.pubsub.PubSubClient;
import org.mojave.connector.gateway.component.PubSubKeys;
import org.mojave.connector.gateway.data.PartiesErrorResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
class HandlePutPartiesErrorCommandHandler implements HandlePutPartiesErrorCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        HandlePutPartiesErrorCommandHandler.class.getName());

    private final PubSubClient pubSubClient;

    public HandlePutPartiesErrorCommandHandler(PubSubClient pubSubClient) {

        Objects.requireNonNull(pubSubClient);

        this.pubSubClient = pubSubClient;
    }

    @Override
    public Output execute(Input input) {

        final var reqId =
            input.payee().fspCode() + "-" + input.partyIdType() + "-" + input.partyId();

        MDC.put("REQ_ID", reqId);

        var channel = PubSubKeys.forPartiesError(
            input.payee(), input.partyIdType(), input.partyId(), input.subId());

        this.pubSubClient.publish(
            channel,
            new PartiesErrorResult(
                input.partyIdType(), input.partyId(), input.subId(),
                input.errorInformationObject()));

        MDC.remove("REQ_ID");

        return new Output();
    }

}
