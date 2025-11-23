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

package io.mojaloop.platform.core.transfer.service.controller.event.listener;

import io.mojaloop.core.transfer.contract.command.GetTransfersCommand;
import io.mojaloop.platform.core.transfer.service.controller.event.GetTransfersEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class GetTransfersEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetTransfersEventListener.class);

    private final GetTransfersCommand getTransfers;

    public GetTransfersEventListener(GetTransfersCommand getTransfers) {

        assert getTransfers != null;

        this.getTransfers = getTransfers;
    }

    @Async
    @EventListener
    public void onGetTransfersEvent(GetTransfersEvent event) {

        LOGGER.info("Start handling GetTransfersEvent : ({})", event);

        var output = this.getTransfers.execute(event.getPayload());

        LOGGER.info("Done handling GetTransfersEvent : ({}), output : ({})", event, output);
    }

}
