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

package org.mojave.core.lookup.service.controller.event.listener;

import org.mojave.core.lookup.contract.command.PutPartiesCommand;
import org.mojave.core.lookup.service.controller.event.PutPartiesEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PutPartiesEventListener {

    private final PutPartiesCommand putParties;

    public PutPartiesEventListener(PutPartiesCommand putParties) {

        assert putParties != null;

        this.putParties = putParties;
    }

    @Async
    @EventListener
    public void onPutPartiesEvent(PutPartiesEvent event) {

        var output = this.putParties.execute(event.getPayload());
    }

}
