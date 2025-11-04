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

package io.mojaloop.core.transaction.intercom.controller;

import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.contract.exception.TransactionIdNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AddStepController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddStepController.class);

    private final AddStepCommand addStepCommand;

    public AddStepController(final AddStepCommand addStepCommand) {

        assert addStepCommand != null;
        this.addStepCommand = addStepCommand;
    }

    @PostMapping("/transactions/add-step")
    public AddStepCommand.Output execute(@Valid @RequestBody final AddStepCommand.Input input) throws TransactionIdNotFoundException {

        LOGGER.info("Entering AddStepCommand.execute: input : {}", input);

        final var output = this.addStepCommand.execute(input);

        LOGGER.info("Exiting AddStepCommand.execute: {}", output);

        return output;
    }

}
