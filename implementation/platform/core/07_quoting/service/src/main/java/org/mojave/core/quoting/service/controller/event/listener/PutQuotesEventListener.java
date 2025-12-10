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

package org.mojave.core.quoting.service.controller.event.listener;

import org.mojave.core.quoting.contract.command.PutQuotesCommand;
import org.mojave.core.quoting.service.controller.event.PutQuotesEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PutQuotesEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutQuotesEventListener.class);

    private final PutQuotesCommand putQuotes;

    public PutQuotesEventListener(PutQuotesCommand putQuotes) {

        assert putQuotes != null;

        this.putQuotes = putQuotes;
    }

    @Async
    @EventListener
    public void onPutQuotesEvent(PutQuotesEvent event) {

        LOGGER.info("Start handling PutQuotesEvent : ({})", event);

        var output = this.putQuotes.execute(event.getPayload());

        LOGGER.info("Done handling PutQuotesEvent : ({}), output : ({})", event, output);
    }

}
