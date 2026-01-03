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
package org.mojave.rail.fspiop.transfer.service.controller.event;

import lombok.Getter;
import org.mojave.component.misc.spring.event.DomainEvent;
import org.mojave.rail.fspiop.transfer.contract.command.PutTransfersCommand;

@Getter
public class PutTransfersEvent extends DomainEvent<PutTransfersCommand.Input> {

    public PutTransfersEvent(PutTransfersCommand.Input input) {

        super(input);
    }

}
