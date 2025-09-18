/*-
 * ================================================================================
 * Mojaloop OSS
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

import io.mojaloop.connector.gateway.inbound.command.quotes.HandleQuotesErrorCommand;
import io.mojaloop.connector.gateway.inbound.event.PutQuotesErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PutQuotesErrorEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutQuotesErrorEventListener.class);

    private final HandleQuotesErrorCommand handleQuotesError;

    public PutQuotesErrorEventListener(HandleQuotesErrorCommand handleQuotesError) {

        assert null != handleQuotesError;

        this.handleQuotesError = handleQuotesError;
    }

    @Async
    @EventListener
    public void handle(PutQuotesErrorEvent event) {

        LOGGER.info("Handling PutQuotesErrorEvent : {}", event);

        var payload = event.getPayload();

        assert null != payload;

        try {

            this.handleQuotesError.execute(new HandleQuotesErrorCommand.Input(payload.source(),
                                                                              payload.quoteId(),
                                                                              payload.errorInformationObject()));

            LOGGER.info("Done handling PutQuotesErrorEvent : {}", event);

        } catch (Exception e) {

            LOGGER.error("Error handling PutQuotesErrorEvent", e);
        }
    }

}
