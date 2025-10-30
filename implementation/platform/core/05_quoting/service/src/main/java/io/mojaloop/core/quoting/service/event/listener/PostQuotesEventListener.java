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

import io.mojaloop.core.quoting.contract.command.PostQuotesCommand;
import io.mojaloop.core.quoting.service.event.PostQuotesEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PostQuotesEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostQuotesEventListener.class);

    private final PostQuotesCommand postQuotes;

    public PostQuotesEventListener(PostQuotesCommand postQuotes) {

        assert postQuotes != null;

        this.postQuotes = postQuotes;
    }

    @Async
    @EventListener
    public void onPostQuotesEvent(PostQuotesEvent event) {

        LOGGER.info("Start handling PostQuotesEvent : [{}]", event);

        var output = this.postQuotes.execute(event.getPayload());

        LOGGER.info("Done handling PostQuotesEvent : [{}], output : [{}]", event, output);
    }

}
