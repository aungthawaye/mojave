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

package org.mojave.core.participant.admin.controller.api.command.fsp;

import jakarta.validation.Valid;
import org.mojave.core.participant.contract.command.fsp.ActivateEndpointCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class ActivateEndpointController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivateEndpointController.class);

    private final ActivateEndpointCommand activateEndpointCommand;

    public ActivateEndpointController(ActivateEndpointCommand activateEndpointCommand) {

        Objects.requireNonNull(activateEndpointCommand);

        this.activateEndpointCommand = activateEndpointCommand;
    }

    @PostMapping("/participant/fsps/activate-endpoint")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ActivateEndpointCommand.Output execute(
        @Valid @RequestBody ActivateEndpointCommand.Input input) {

        return this.activateEndpointCommand.execute(input);
    }

}
