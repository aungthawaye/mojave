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

import io.mojaloop.core.quoting.contract.command.GetQuotesCommand;
import io.mojaloop.core.quoting.service.event.GetQuotesEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class GetQuotesEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetQuotesEventListener.class);

    private final GetQuotesCommand getQuotes;

    public GetQuotesEventListener(GetQuotesCommand getQuotes) {

        assert getQuotes != null;

        this.getQuotes = getQuotes;
    }

    @Async
    @EventListener
    public void onGetQuotesEvent(GetQuotesEvent event) {

        LOGGER.info("Start handling GetQuotesEvent : [{}]", event);

        var output = this.getQuotes.execute(event.getPayload());

        LOGGER.info("Done handling GetQuotesEvent : [{}], output : [{}]", event, output);
    }

}
