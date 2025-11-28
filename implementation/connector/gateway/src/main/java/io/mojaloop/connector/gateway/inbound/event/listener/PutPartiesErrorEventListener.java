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

package io.mojaloop.connector.gateway.inbound.event.listener;

import io.mojaloop.connector.gateway.inbound.command.parties.HandlePutPartiesErrorCommand;
import io.mojaloop.connector.gateway.inbound.event.PutPartiesErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PutPartiesErrorEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        PutPartiesErrorEventListener.class);

    private final HandlePutPartiesErrorCommand handlePartiesError;

    public PutPartiesErrorEventListener(HandlePutPartiesErrorCommand handlePartiesError) {

        assert null != handlePartiesError;

        this.handlePartiesError = handlePartiesError;
    }

    @Async
    @EventListener
    public void handle(PutPartiesErrorEvent event) {

        LOGGER.info("Handling PutPartiesErrorEvent : {}", event);

        var payload = event.getPayload();

        assert null != payload;

        try {

            this.handlePartiesError.execute(
                new HandlePutPartiesErrorCommand.Input(
                    payload.payee(), payload.partyIdType(), payload.partyId(), payload.subId(),
                    payload.errorInformationObject()));

            LOGGER.info("Done handling PutPartiesErrorEvent : {}", event);

        } catch (Exception e) {

            LOGGER.error("Error handling PutPartiesErrorEvent", e);
        }
    }

}
