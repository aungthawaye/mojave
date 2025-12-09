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

import io.mojaloop.connector.gateway.inbound.command.quotes.HandlePutQuotesResponseCommand;
import io.mojaloop.connector.gateway.inbound.event.PutQuotesEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PutQuotesEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutQuotesEventListener.class);

    private final HandlePutQuotesResponseCommand handleQuotesResponse;

    public PutQuotesEventListener(HandlePutQuotesResponseCommand handleQuotesResponse) {

        assert null != handleQuotesResponse;

        this.handleQuotesResponse = handleQuotesResponse;
    }

    @Async
    @EventListener
    public void handle(PutQuotesEvent event) {

        LOGGER.info("Handling PutQuotesEvent : {}", event);

        var payload = event.getPayload();

        assert null != payload;

        try {

            this.handleQuotesResponse.execute(
                new HandlePutQuotesResponseCommand.Input(
                    payload.payee(), payload.quoteId(), payload.response()));

            LOGGER.info("Done handling PutQuotesEvent : {}", event);

        } catch (Exception e) {

            LOGGER.error("Error handling PutQuotesEvent", e);
        }
    }

}
