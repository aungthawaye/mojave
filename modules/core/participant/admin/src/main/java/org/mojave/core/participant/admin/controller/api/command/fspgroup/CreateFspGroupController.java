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

package org.mojave.core.participant.admin.controller.api.command.fspgroup;

import jakarta.validation.Valid;
import org.mojave.core.participant.contract.command.fsp.CreateFspGroupCommand;
import org.mojave.core.participant.contract.exception.fsp.FspGroupNameAlreadyExistsException;
import org.mojave.core.participant.contract.exception.fsp.FspGroupNameRequiredException;
import org.mojave.core.participant.contract.exception.fsp.FspGroupNameTooLongException;
import org.mojave.core.participant.contract.exception.fsp.FspIdNotFoundException;
import org.mojave.core.participant.contract.exception.fsp.TerminatedFspIdException;
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
public class CreateFspGroupController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        CreateFspGroupController.class.getName());

    private final CreateFspGroupCommand createFspGroupCommand;

    public CreateFspGroupController(final CreateFspGroupCommand createFspGroupCommand) {

        Objects.requireNonNull(createFspGroupCommand);

        this.createFspGroupCommand = createFspGroupCommand;
    }

    @PostMapping("/participant/fsp-groups/create-fsp-group")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CreateFspGroupCommand.Output execute(
        @Valid @RequestBody final CreateFspGroupCommand.Input input) throws
                                                                     FspGroupNameRequiredException,
                                                                     FspGroupNameTooLongException,
                                                                     FspGroupNameAlreadyExistsException,
                                                                     FspIdNotFoundException,
                                                                     TerminatedFspIdException {

        return this.createFspGroupCommand.execute(input);
    }

}
