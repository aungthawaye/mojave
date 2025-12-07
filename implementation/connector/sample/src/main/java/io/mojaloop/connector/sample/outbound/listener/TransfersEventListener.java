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

package io.mojaloop.connector.sample.outbound.listener;

import io.mojaloop.connector.gateway.outbound.event.TransfersErrorEvent;
import io.mojaloop.connector.gateway.outbound.event.TransfersRequestEvent;
import io.mojaloop.connector.gateway.outbound.event.TransfersResponseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class TransfersEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransfersEventListener.class);

    @Async
    @EventListener
    public void handle(TransfersRequestEvent event) {

    }

    @Async
    @EventListener
    public void handle(TransfersResponseEvent event) {

    }

    @Async
    @EventListener
    public void handle(TransfersErrorEvent event) {

    }

}
