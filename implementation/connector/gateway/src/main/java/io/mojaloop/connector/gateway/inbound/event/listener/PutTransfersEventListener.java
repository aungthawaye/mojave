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

import io.mojaloop.connector.gateway.inbound.command.transfers.HandlePutTransfersResponseCommand;
import io.mojaloop.connector.gateway.inbound.event.PutTransfersEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PutTransfersEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutTransfersEventListener.class);

    private final HandlePutTransfersResponseCommand handleTransfersResponse;

    public PutTransfersEventListener(HandlePutTransfersResponseCommand handleTransfersResponse) {

        assert null != handleTransfersResponse;

        this.handleTransfersResponse = handleTransfersResponse;
    }

    @Async
    @EventListener
    public void handle(PutTransfersEvent event) {

        LOGGER.info("Handling PutTransfersEvent : {}", event);

        var payload = event.getPayload();

        assert null != payload;

        try {

            this.handleTransfersResponse.execute(
                new HandlePutTransfersResponseCommand.Input(
                    payload.payee(), payload.transferId(),
                    payload.response()));

            LOGGER.info("Done handling PutTransfersEvent : {}", event);

        } catch (Exception e) {

            LOGGER.error("Error handling PutTransfersEvent", e);
        }
    }

}
