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
package org.mojave.core.wallet.admin.controller.api.command.position;

import jakarta.validation.Valid;
import org.mojave.core.wallet.contract.command.position.RollbackReservationCommand;
import org.mojave.core.wallet.contract.exception.position.FailedToRollbackReservationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RollbackPositionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RollbackPositionController.class);

    private final RollbackReservationCommand rollbackReservationCommand;

    public RollbackPositionController(final RollbackReservationCommand rollbackReservationCommand) {

        assert rollbackReservationCommand != null;

        this.rollbackReservationCommand = rollbackReservationCommand;
    }

    @PostMapping("/positions/rollback")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RollbackReservationCommand.Output execute(
        @Valid @RequestBody final RollbackReservationCommand.Input input)
        throws FailedToRollbackReservationException {

        LOGGER.info("Entering RollbackReservationCommand.execute: input : {}", input);

        final var output = this.rollbackReservationCommand.execute(input);

        LOGGER.info("Exiting RollbackReservationCommand.execute: {}", output);

        return output;
    }

}
