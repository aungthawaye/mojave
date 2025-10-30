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

package io.mojaloop.core.quoting.service.event.listener;

import io.mojaloop.core.quoting.contract.command.PutQuotesErrorCommand;
import io.mojaloop.core.quoting.service.event.PutQuotesErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PutQuotesErrorEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutQuotesErrorEventListener.class);

    private final PutQuotesErrorCommand putQuotesError;

    public PutQuotesErrorEventListener(PutQuotesErrorCommand putQuotesError) {

        assert putQuotesError != null;

        this.putQuotesError = putQuotesError;
    }

    @Async
    @EventListener
    public void onPutQuotesErrorEvent(PutQuotesErrorEvent event) {

        LOGGER.info("Start handling PutQuotesErrorEvent : [{}]", event);

        var output = this.putQuotesError.execute(event.getPayload());

        LOGGER.info("Done handling PutQuotesErrorEvent : [{}], output : [{}]", event, output);
    }

}
