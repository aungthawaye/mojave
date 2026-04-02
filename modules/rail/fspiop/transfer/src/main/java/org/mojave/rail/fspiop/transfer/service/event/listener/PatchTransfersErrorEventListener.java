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

package org.mojave.rail.fspiop.transfer.service.event.listener;

import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.rail.fspiop.transfer.contract.command.PatchTransfersErrorCommand;
import org.mojave.rail.fspiop.transfer.service.event.PatchTransfersErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PatchTransfersErrorEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        PatchTransfersErrorEventListener.class);

    private final PatchTransfersErrorCommand patchTransfersError;

    public PatchTransfersErrorEventListener(PatchTransfersErrorCommand patchTransfersError) {

        Objects.requireNonNull(patchTransfersError);
        this.patchTransfersError = patchTransfersError;
    }

    @Async
    @EventListener
    public void onPatchTransfersErrorEvent(PatchTransfersErrorEvent event) {

        LOGGER.info("PatchTransfersErrorEvent : event : ({})", ObjectLogger.log(event));

        var output = this.patchTransfersError.execute(event.getPayload());

        LOGGER.info(
            "PatchTransfersErrorEvent : ({}), output : ({})", ObjectLogger.log(event),
            ObjectLogger.log(output));
    }

}
