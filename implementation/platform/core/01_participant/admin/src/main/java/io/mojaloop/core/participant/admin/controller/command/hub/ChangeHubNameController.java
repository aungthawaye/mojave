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

package io.mojaloop.core.participant.admin.controller.command.hub;

import io.mojaloop.core.participant.contract.command.hub.ChangeHubNameCommand;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChangeHubNameController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeHubNameController.class);

    private final ChangeHubNameCommand changeHubNameCommand;

    public ChangeHubNameController(ChangeHubNameCommand changeHubNameCommand) {

        assert changeHubNameCommand != null;

        this.changeHubNameCommand = changeHubNameCommand;
    }

    @PostMapping("/hubs/change-name")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ChangeHubNameCommand.Output execute(@Valid @RequestBody ChangeHubNameCommand.Input input) {

        return this.changeHubNameCommand.execute(input);
    }

}
