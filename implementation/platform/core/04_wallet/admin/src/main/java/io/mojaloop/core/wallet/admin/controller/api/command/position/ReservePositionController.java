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
package io.mojaloop.core.wallet.admin.controller.api.command.position;

import io.mojaloop.core.wallet.contract.command.position.ReservePositionCommand;
import io.mojaloop.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import io.mojaloop.core.wallet.contract.exception.position.PositionLimitExceededException;
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
public class ReservePositionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservePositionController.class);

    private final ReservePositionCommand reservePositionCommand;

    public ReservePositionController(final ReservePositionCommand reservePositionCommand) {

        assert reservePositionCommand != null;

        this.reservePositionCommand = reservePositionCommand;
    }

    @PostMapping("/positions/reserve")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ReservePositionCommand.Output execute(@Valid @RequestBody final ReservePositionCommand.Input input)
        throws NoPositionUpdateForTransactionException, PositionLimitExceededException {

        LOGGER.info("Entering ReservePositionCommand.execute: input : {}", input);

        final var output = this.reservePositionCommand.execute(input);

        LOGGER.info("Exiting ReservePositionCommand.execute: {}", output);

        return output;
    }
}
