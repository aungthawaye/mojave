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
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class GetPartiesEventListener {

    private final GetPartiesCommand getParties;

    public GetPartiesEventListener(GetPartiesCommand getParties) {

        assert getParties != null;

        this.getParties = getParties;
    }

    @Async
    @EventListener
    public void onGetPartiesEvent(GetPartiesEvent event) {

        this.getParties.execute(event.getPayload());
    }

}
