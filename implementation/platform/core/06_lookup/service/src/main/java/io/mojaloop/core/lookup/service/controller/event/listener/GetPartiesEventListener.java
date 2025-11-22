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

import io.mojaloop.core.lookup.contract.command.GetPartiesCommand;
import io.mojaloop.core.lookup.service.controller.event.GetPartiesEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class GetPartiesEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPartiesEventListener.class);

    private final GetPartiesCommand getParties;

    public GetPartiesEventListener(GetPartiesCommand getParties) {

        assert getParties != null;

        this.getParties = getParties;
    }

    @Async
    @EventListener
    public void onGetPartiesEvent(GetPartiesEvent event) {

        LOGGER.info("Start handling GetPartiesEvent : [{}]", event);

        var output = this.getParties.execute(event.getPayload());

        LOGGER.info("Done handling GetPartiesEvent : [{}], output : [{}]", event, output);
    }

}
