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

import io.mojaloop.core.transaction.contract.command.CloseTransactionCommand;
import io.mojaloop.core.transaction.contract.exception.TransactionIdNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CloseTransactionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloseTransactionController.class);

    private final CloseTransactionCommand closeTransactionCommand;

    public CloseTransactionController(final CloseTransactionCommand closeTransactionCommand) {

        assert closeTransactionCommand != null;
        this.closeTransactionCommand = closeTransactionCommand;
    }

    @PostMapping("/transactions/close")
    public CloseTransactionCommand.Output execute(@Valid @RequestBody final CloseTransactionCommand.Input input) throws TransactionIdNotFoundException {

        LOGGER.info("Entering CloseTransactionCommand.execute: input : {}", input);

        final var output = this.closeTransactionCommand.execute(input);

        LOGGER.info("Exiting CloseTransactionCommand.execute: {}", output);

        return output;
    }

}
