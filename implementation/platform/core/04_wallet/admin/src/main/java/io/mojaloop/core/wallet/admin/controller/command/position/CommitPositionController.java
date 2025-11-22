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
package io.mojaloop.core.wallet.admin.controller.command.position;

import io.mojaloop.core.wallet.contract.command.position.CommitReservationCommand;
import io.mojaloop.core.wallet.contract.exception.position.FailedToCommitReservationException;
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
public class CommitPositionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommitPositionController.class);

    private final CommitReservationCommand commitReservationCommand;

    public CommitPositionController(final CommitReservationCommand commitReservationCommand) {

        assert commitReservationCommand != null;

        this.commitReservationCommand = commitReservationCommand;
    }

    @PostMapping("/positions/commit")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CommitReservationCommand.Output execute(@Valid @RequestBody final CommitReservationCommand.Input input) throws FailedToCommitReservationException {

        LOGGER.info("Entering CommitReservationCommand.execute: input : {}", input);

        final var output = this.commitReservationCommand.execute(input);

        LOGGER.info("Exiting CommitReservationCommand.execute: {}", output);

        return output;
    }

}
