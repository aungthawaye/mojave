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

package io.mojaloop.core.lookup.service.controller.event.listener;

import io.mojaloop.core.lookup.contract.command.PutPartiesErrorCommand;
import io.mojaloop.core.lookup.service.controller.event.PutPartiesErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PutPartiesErrorEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutPartiesErrorEventListener.class);

    private final PutPartiesErrorCommand putPartiesError;

    public PutPartiesErrorEventListener(PutPartiesErrorCommand putPartiesError) {

        assert putPartiesError != null;

        this.putPartiesError = putPartiesError;
    }

    @Async
    @EventListener
    public void onPutPartiesErrorEvent(PutPartiesErrorEvent event) {

        LOGGER.info("Start handling PutPartiesErrorEvent : [{}]", event);

        var output = this.putPartiesError.execute(event.getPayload());

        LOGGER.info("Done handling PutPartiesErrorEvent : [{}], output : [{}]", event, output);
    }

}
