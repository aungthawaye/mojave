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
package org.mojave.connector.gateway.inbound.event.listener;

import org.mojave.connector.gateway.inbound.command.parties.HandleGetPartiesRequestCommand;
import org.mojave.connector.gateway.inbound.event.GetPartiesEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class GetPartiesEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPartiesEventListener.class);

    private final HandleGetPartiesRequestCommand handlePartiesRequest;

    public GetPartiesEventListener(HandleGetPartiesRequestCommand handlePartiesRequest) {

        assert null != handlePartiesRequest;

        this.handlePartiesRequest = handlePartiesRequest;
    }

    @Async
    @EventListener
    public void handle(GetPartiesEvent event) {

        LOGGER.info("Handling GetPartiesEvent : {}", event);

        var payload = event.getPayload();

        assert null != payload;

        try {

            this.handlePartiesRequest.execute(
                new HandleGetPartiesRequestCommand.Input(
                    payload.payer(), payload.partyIdType(), payload.partyId(), payload.subId()));

            LOGGER.info("Done handling GetPartiesEvent : {}", event);

        } catch (Exception e) {

            LOGGER.error("Error handling GetPartiesEvent", e);
        }
    }

}
