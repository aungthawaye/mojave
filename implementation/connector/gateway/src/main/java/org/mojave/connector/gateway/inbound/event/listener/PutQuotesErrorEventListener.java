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
package org.mojave.connector.gateway.inbound.event.listener;

import org.mojave.connector.gateway.inbound.command.quotes.HandlePutQuotesErrorCommand;
import org.mojave.connector.gateway.inbound.event.PutQuotesErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PutQuotesErrorEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutQuotesErrorEventListener.class);

    private final HandlePutQuotesErrorCommand handleQuotesError;

    public PutQuotesErrorEventListener(HandlePutQuotesErrorCommand handleQuotesError) {

        assert null != handleQuotesError;

        this.handleQuotesError = handleQuotesError;
    }

    @Async
    @EventListener
    public void handle(PutQuotesErrorEvent event) {

        var payload = event.getPayload();

        assert null != payload;

        try {

            this.handleQuotesError.execute(
                new HandlePutQuotesErrorCommand.Input(
                    payload.payee(), payload.quoteId(), payload.errorInformationObject()));

        } catch (Exception e) {

            LOGGER.error("Error:", e);
        }
    }

}
