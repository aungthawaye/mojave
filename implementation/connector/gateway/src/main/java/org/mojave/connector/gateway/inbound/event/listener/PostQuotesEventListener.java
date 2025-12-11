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

import org.mojave.connector.gateway.inbound.command.quotes.HandlePostQuotesRequestCommand;
import org.mojave.connector.gateway.inbound.event.PostQuotesEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PostQuotesEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostQuotesEventListener.class);

    private final HandlePostQuotesRequestCommand handleQuotesRequest;

    public PostQuotesEventListener(HandlePostQuotesRequestCommand handleQuotesRequest) {

        assert null != handleQuotesRequest;

        this.handleQuotesRequest = handleQuotesRequest;
    }

    @Async
    @EventListener
    public void handle(PostQuotesEvent event) {

        LOGGER.info("Handling PostQuotesEvent : {}", event);

        var payload = event.getPayload();

        assert null != payload;

        try {

            this.handleQuotesRequest.execute(
                new HandlePostQuotesRequestCommand.Input(
                    payload.payer(), payload.quoteId(), payload.request()));

            LOGGER.info("Done handling PostQuotesEvent : {}", event);

        } catch (Exception e) {

            LOGGER.error("Error handling PostQuotesEvent", e);
        }
    }

}
